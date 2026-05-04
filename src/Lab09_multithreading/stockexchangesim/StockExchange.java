package Lab09_multithreading.stockexchangesim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *   1. newFixedThreadPool(3) for 5 Traders
 *   ────────────────────────────────────────────────────
 *   5 tasks, only 3 threads — demonstrates thread REUSE.
 *   The 4th and 5th trader tasks queue up and run when a thread becomes free.
 *   "Like a taxi service instead of buying a new car for every trip."
 *
 *   2. newSingleThreadExecutor() for MatchingEngine
 *   ────────────────────────────────────────────────────────────
 *   "Exactly one thread, tasks run sequentially."
 *   We only ever want ONE engine — single-thread pool enforces that.
 *   If the engine thread crashes, the executor creates a replacement automatically.
 *
 *   3. submit() not execute()
 *   ─────────────────────────────────────
 *   "Always prefer submit(). Exceptions don't vanish silently."
 *   submit() returns a Future — we store all Futures in a list.
 *   After shutdown, we call future.get(timeout) on each one to:
 *     a) retrieve the summary string
 *     b) surface any exception that occurred inside the thread
 *   execute() returns void — exceptions would silently disappear.
 *
 *   4. Callable + Future
 *   ─────────────────────────────────
 *   Both Trader and MatchingEngine implement Callable<String>.
 *   Each returns a summary (e.g. "Trader-1 placed 42 orders.").
 *   We collect these via future.get(10, TimeUnit.SECONDS) after shutdown.
 *
 *   5. Shutdown order
 *   ──────────────────────────────
 *   The ORDER of shutdown matters — if you stop the pool before setting the flag,
 *   threads are interrupted mid-task and may not clean up properly.
 *
 *     Step 1: exchangeOpen = false    → threads stop looping on next iteration
 *     Step 2: orderBook.wakeAll()     → unblocks threads stuck in Condition.await()
 *     Step 3: traderPool.shutdown()   → stop accepting new trader tasks
 *     Step 4: enginePool.shutdown()   → stop accepting new engine tasks
 *     Step 5: future.get(timeout)     → collect results / surface exceptions
 *     Step 6: awaitTermination()      → wait for running tasks to finish
 *     Step 7: shutdownNow() if needed → force kill if timeout exceeded
 *     Step 8: dashboard.stop()        → stop last so final stats print
 */
public class StockExchange {

    // How long to run the exchange before shutting down
    private static final int RUN_SECONDS = 15;

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   STOCK EXCHANGE SIMULATOR — OPEN");
        System.out.println("   Running for " + RUN_SECONDS + " seconds...");
        System.out.println("========================================\n");

        // Shared objects
        OrderBook     orderBook  = new OrderBook(100);
        ExchangeStats stats      = new ExchangeStats();
        AtomicBoolean exchangeOpen  = new AtomicBoolean(true);
        AtomicInteger idCounter     = new AtomicInteger(0);

        // Thread pools
        // Fixed pool of 3 threads for 5 traders.
        ExecutorService traderPool = Executors.newFixedThreadPool(3);

        // Single thread for the matching engine — only one engine ever runs.
        ExecutorService enginePool = Executors.newSingleThreadExecutor();

        //  Dashboard (ScheduledExecutorService )
        Dashboard dashboard = new Dashboard(stats, orderBook);
        dashboard.start();

        // Submit Traders using submit()
        // Callable<String> — result captured in Future<String>
        // submit() not execute() — exceptions are captured, not silently swallowed
        List<Future<String>> traderFutures = new ArrayList<>();
        String[] traderNames = {"Trader-1", "Trader-2", "Trader-3", "Trader-4", "Trader-5"};

        for (String name : traderNames) {
            Trader trader = new Trader(name, orderBook, idCounter, exchangeOpen);
            Future<String> future = traderPool.submit(trader);   // submit Callable
            traderFutures.add(future);
        }

        // Submit MatchingEngine using submit()
        MatchingEngine engine = new MatchingEngine(orderBook, stats, exchangeOpen);
        Future<String> engineFuture = enginePool.submit(engine);

        //  Run for RUN_SECONDS
        Thread.sleep(RUN_SECONDS * 1000L);

        //  SHUTDOWN SEQUENCE
        System.out.println("\n===== EXCHANGE CLOSING =====");

        // Step 1: Signal all threads to stop looping
        exchangeOpen.set(false);

        // Step 2: Unblock engine / traders from Condition.await() in OrderBook
        orderBook.wakeAll();

        // Step 3: Stop accepting new tasks into trader pool
        traderPool.shutdown();

        // Step 4: Stop accepting new tasks into engine pool
        enginePool.shutdown();

        // Step 5: Collect Future results from traders
        System.out.println("\n--- Trader Summaries ---");
        for (Future<String> f : traderFutures) {
            try {
                // get(10s) — give each trader up to 10 seconds to finish
                String summary = f.get(10, TimeUnit.SECONDS);
                System.out.println(summary);
            } catch (TimeoutException e) {
                System.out.println("Trader timed out on shutdown.");
                f.cancel(true);   // interrupt the trader thread
            } catch (ExecutionException e) {
                // Surface exception that happened inside the Callable
                System.out.println("Trader threw exception: " + e.getCause());
            }
        }

        // Step 5b: Collect Future result from engine
        System.out.println("\n--- Engine Summary ---");
        try {
            String engineSummary = engineFuture.get(15, TimeUnit.SECONDS);
            System.out.println(engineSummary);
        } catch (TimeoutException e) {
            System.out.println("Engine timed out on shutdown.");
            engineFuture.cancel(true);
        } catch (ExecutionException e) {
            System.out.println("Engine threw exception: " + e.getCause());
        }

        // Step 6 + 7: awaitTermination → shutdownNow if needed
        shutdownPool("Trader Pool", traderPool);
        shutdownPool("Engine Pool", enginePool);

        // Step 8: Stop dashboard last (so final stats are visible)
        dashboard.stop();

        // Final Summary
        System.out.println("\n========================================");
        System.out.println("         FINAL EXCHANGE REPORT");
        System.out.println("========================================");
        System.out.println("Total orders submitted: " + idCounter.get());
        System.out.println(stats.getSnapshot());
        System.out.println("Unmatched: "
                + orderBook.getBuyOrderCount()  + " buy + "
                + orderBook.getSellOrderCount() + " sell");
        System.out.println("All pools shut down successfully.");
        System.out.println("========================================");
    }

    /**
     * shutdown pattern .
     *
     * shutdown()            → stop accepting new tasks, let running tasks finish
     * awaitTermination(30s) → wait up to 30s for tasks to complete
     * shutdownNow()         → interrupt all running threads if timeout hit
     *   "shutdown() = graceful. shutdownNow() = forceful. Always call one of them."
     */
    private static void shutdownPool(String name, ExecutorService pool) {
        // shutdown() already called before this method — just await
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println(name + " did not terminate in time — forcing shutdown.");
                List<Runnable> pending = pool.shutdownNow();
                System.out.println(name + " had " + pending.size() + " pending tasks.");
                // Wait a bit more after forceful shutdown
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println(name + " could not be terminated.");
                }
            } else {
                System.out.println(name + " shut down cleanly.");
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
