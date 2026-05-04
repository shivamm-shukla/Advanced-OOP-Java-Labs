package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *   "ScheduledExecutorService — like cron jobs in Java, no more Thread.sleep() in a loop"
 *
 *   scheduleAtFixedRate(task, initialDelay, period, unit):
 *     Fires at t=0, t=2s, t=4s, ... regardless of how long the task takes.
 *     If the task takes longer than the period, the next run is delayed (no parallel overlap).
 *
 *   The old alternative was:
 *       while (true) { printStats(); Thread.sleep(2000); }
 *   That's fragile — sleep is not precise and ties up a whole thread.
 *   ScheduledExecutorService is the professional way.
 *
 *   Shutdown:
 *   ─────────────────────
 *   We call shutdown() → awaitTermination() → shutdownNow() — the full pattern from lecture.
 *   Dashboard is stopped LAST so it can still print a final snapshot before exit.
 */
public class Dashboard {

    private final ExchangeStats stats;
    private final OrderBook orderBook;
    private final ScheduledExecutorService scheduler;

    public Dashboard(ExchangeStats stats, OrderBook orderBook) {
        this.stats = stats;
        this.orderBook = orderBook;
        // Single background thread is enough , dashboard just reads and prints
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Starts the periodic dashboard.
     * scheduleAtFixedRate: print immediately (delay=0), then every 2 seconds.
     *
     * The lambda runs on the scheduler thread — ExchangeStats.getSnapshot()
     * is protected by a ReadWriteLock, so this is safe.
     */
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n===== EXCHANGE DASHBOARD =====");
            System.out.println(stats.getSnapshot());
            System.out.println("Pending orders: " + orderBook.getOrderCount()
                    + " (buy=" + orderBook.getBuyOrderCount()
                    + " sell=" + orderBook.getSellOrderCount() + ")");
            System.out.println("==============================\n");
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Graceful shutdown :
     *   1. shutdown()            — stop accepting new scheduled fires
     *   2. awaitTermination(5s)  — wait for current print to finish
     *   3. shutdownNow()         — force if it takes too long
     */
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}