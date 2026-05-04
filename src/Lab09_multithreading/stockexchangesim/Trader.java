package Lab09_multithreading.stockexchangesim;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *   Callable<String>
 *   ────────────────────────────
 *   Runnable.run() returns void and cannot throw checked exceptions.
 *   Callable<V>.call() returns V (here, a summary String) and CAN throw exceptions.
 *   Submitted via pool.submit(callable) → Future<String>.
 *   Main thread later calls future.get() to retrieve the summary.
 *
 *   AtomicBoolean — exchangeOpen
 *   ────────────────────────────────────────────
 *   All 5 trader threads AND the engine share this single AtomicBoolean.
 *   When main sets exchangeOpen.set(false), every thread sees it on its next
 *   loop iteration — visibility is guaranteed by the Atomic class (like volatile).
 *
 *   AtomicInteger — orderIdCounter
 *   ───────────────────────────────────────────
 *   Shared across all trader threads. Each trader calls orderIdCounter.incrementAndGet()
 *   which is one atomic CAS instruction — no two traders ever get the same ID.
 *   If this were a plain int, two traders could read the same value and produce duplicate IDs.
 */
public class Trader implements Callable<String> {

    // Stock symbols and their approximate base prices (rupees)
    private static final String[] STOCKS      = {"RELIANCE", "TCS", "INFY", "HDFC", "WIPRO"};
    private static final double[] BASE_PRICES = { 2890.00,  3520.00, 1425.00, 1650.00, 450.00};

    private final String        name;
    private final OrderBook     orderBook;
    private final AtomicInteger orderIdCounter;   // shared across all traders
    private final AtomicBoolean exchangeOpen;     // shutdown signal

    private int ordersPlaced = 0;   // local — only this thread writes to it

    public Trader(String name, OrderBook orderBook,
                  AtomicInteger orderIdCounter, AtomicBoolean exchangeOpen) {
        this.name           = name;
        this.orderBook      = orderBook;
        this.orderIdCounter = orderIdCounter;
        this.exchangeOpen   = exchangeOpen;
    }

    /**
     * call() is the Callable equivalent of Runnable.run().
     *
     * Loop: while exchange is open, generate a random order and submit it.
     *   - Random stock index
     *   - Price = base ± up to 5% (simulates market fluctuation)
     *   - Quantity = 1 to 50 shares
     *   - Type = BUY or SELL randomly
     *   - orderId via AtomicInteger (unique, no lock required)
     *   - Sleep 50–200ms between orders (simulate real trader latency)
     *
     * Returns a summary String that the Future captures for Task 5 reporting.
     */
    @Override
    public String call() throws Exception {
        Random random = new Random();

        while (exchangeOpen.get()) {
            // Pick a random stock
            int stockIdx = random.nextInt(STOCKS.length);
            String stock      = STOCKS[stockIdx];
            double basePrice  = BASE_PRICES[stockIdx];

            // Price ± up to 5% of base (so BUY and SELL prices overlap, triggering matches)
            double fluctuation = basePrice * 0.05 * (random.nextDouble() * 2 - 1);
            double price       = Math.round((basePrice + fluctuation) * 100.0) / 100.0;

            int quantity = random.nextInt(50) + 1;   // 1..50

            Order.OrderType type = random.nextBoolean()
                    ? Order.OrderType.BUY : Order.OrderType.SELL;

            // AtomicInteger.incrementAndGet() — atomic, no two traders share an ID
            String orderId = String.format("ORD-%04d", orderIdCounter.incrementAndGet());

            Order order = new Order(orderId, name, type, stock, price, quantity);

            try {
                orderBook.addOrder(order);   // blocks if book is full (Condition.await)
                System.out.printf("[%-10s] Submitted: %s%n", name, order);
                ordersPlaced++;
            } catch (InterruptedException e) {
                // Interrupted during addOrder (e.g. during shutdown) — restore flag and exit
                Thread.currentThread().interrupt();
                break;
            }

            // Random sleep 50–200ms between orders
            try {
                Thread.sleep(50 + random.nextInt(150));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("[%s] Stopping. Placed %d orders.%n", name, ordersPlaced);
        return name + " placed " + ordersPlaced + " orders.";
    }
}