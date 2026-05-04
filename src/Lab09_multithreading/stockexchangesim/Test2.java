package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests the OrderBook with two raw threads (no ExecutorService yet).
 * Key things to observe:
 *   - Producer blocks when book hits capacity (5)
 *   - Consumer matches pairs and frees space
 *   - No deadlocks, program exits cleanly
 */
public class Test2 {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Task 2 Test — OrderBook with Conditions ===");

        OrderBook book = new OrderBook(5);   // small capacity to force blocking
        AtomicBoolean open = new AtomicBoolean(true);

        // Producer: adds 20 orders, alternating BUY/SELL, 100ms apart
        Thread producer = new Thread(() -> {
            try {
                // Use SAME stock with BUY price always >= SELL price so matches occur
                for (int i = 0; i < 20; i++) {
                    Order.OrderType type = (i % 2 == 0)
                            ? Order.OrderType.BUY : Order.OrderType.SELL;
                    // BUY at 1010, SELL at 1000 — BUY >= SELL on same stock → match
                    double price = (type == Order.OrderType.BUY) ? 1010.0 : 1000.0;
                    Order order = new Order("ORD-" + i, "TestTrader",
                            type, "TCS", price, 10);
                    book.addOrder(order);
                    System.out.println("[Producer] Added: " + order);
                    Thread.sleep(80);
                }
                System.out.println("[Producer] Done adding all orders.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "ProducerThread");

        // Consumer: tries to take 5 matches, 300ms apart
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    MatchedPair pair = book.takeBestMatch(open);
                    if (pair != null) {
                        System.out.println("[Consumer] MATCHED: " + pair);
                    }
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "ConsumerThread");

        producer.start();
        consumer.start();

        producer.join();       // wait for producer to finish
        open.set(false);       // signal shutdown
        book.wakeAll();        // unblock consumer if it's in await()
        consumer.join();

        System.out.println("[TestTask2] Remaining orders in book: " + book.getOrderCount());
        System.out.println("[TestTask2] Test complete.");
    }
}