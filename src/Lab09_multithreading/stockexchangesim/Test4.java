package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests: Traders + MatchingEngine + Dashboard all running together via raw threads.
 * Runs for 8 seconds then shuts down cleanly.
 */
public class Test4 {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Task 4 Test — Engine + Dashboard ===");

        OrderBook     book      = new OrderBook(100);
        ExchangeStats stats     = new ExchangeStats();
        AtomicBoolean open      = new AtomicBoolean(true);
        AtomicInteger idCounter = new AtomicInteger(0);

        // Dashboard — uses ScheduledExecutorService internally
        Dashboard dashboard = new Dashboard(stats, book);
        dashboard.start();

        // Engine in its own raw thread
        Thread engine = new Thread(() -> {
            try { new MatchingEngine(book, stats, open).call(); }
            catch (Exception e) { e.printStackTrace(); }
        }, "EngineThread");
        engine.start();

        // Two traders as raw threads
        Thread t1 = new Thread(() -> {
            try { new Trader("T-1", book, idCounter, open).call(); }
            catch (Exception e) { e.printStackTrace(); }
        }, "Trader-1-Thread");

        Thread t2 = new Thread(() -> {
            try { new Trader("T-2", book, idCounter, open).call(); }
            catch (Exception e) { e.printStackTrace(); }
        }, "Trader-2-Thread");

        t1.start();
        t2.start();

        // Let exchange run for 8 seconds
        Thread.sleep(8000);

        System.out.println("\n===== EXCHANGE CLOSING (Task 4) =====");
        open.set(false);    // tell all threads to stop looping
        book.wakeAll();     // unblock engine from Condition.await()

        t1.join();
        t2.join();
        engine.join();
        dashboard.stop();   // stop last so it can print a final dashboard

        System.out.println("\n=== Final Stats ===");
        System.out.println(stats.getSnapshot());
        System.out.println("Unmatched: "
                + book.getBuyOrderCount() + " buy + "
                + book.getSellOrderCount() + " sell");
        System.out.println("[TestTask4] Complete.");
    }
}