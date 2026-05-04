package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Two traders as raw threads, run for 3 seconds, then shut down.
 */
public class Test3 {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Task 3 Test — Trader + ExchangeStats ===");

        OrderBook     book      = new OrderBook(50);
        ExchangeStats stats     = new ExchangeStats();
        AtomicBoolean open      = new AtomicBoolean(true);
        AtomicInteger idCounter = new AtomicInteger(0);

        // Two traders as plain threads (ExecutorService comes in Task 5)
        Thread t1 = new Thread(() -> {
            try { new Trader("Trader-1", book, idCounter, open).call(); }
            catch (Exception e) { e.printStackTrace(); }
        }, "Trader-1-Thread");

        Thread t2 = new Thread(() -> {
            try { new Trader("Trader-2", book, idCounter, open).call(); }
            catch (Exception e) { e.printStackTrace(); }
        }, "Trader-2-Thread");

        t1.start();
        t2.start();

        Thread.sleep(3000);    // let them run for 3 seconds

        open.set(false);       // signal shutdown
        book.wakeAll();        // unblock any threads in Condition.await()

        t1.join();
        t2.join();

        System.out.println("=== Task 3 Results ===");
        System.out.println("Total order IDs generated: " + idCounter.get());
        System.out.println("Pending orders in book:    " + book.getOrderCount());
        System.out.println("[TestTask3] Test complete.");
    }
}