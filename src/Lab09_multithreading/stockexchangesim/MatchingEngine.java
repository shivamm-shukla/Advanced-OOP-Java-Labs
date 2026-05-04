package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *   Callable<String>
 *   ────────────────────────────
 *   Returns a summary of how many trades were executed.
 *   Submitted to a newSingleThreadExecutor() in Task 5 — only ONE engine ever runs.
 *   Future<String> lets main collect the result after shutdown.
 *
 *   Producer-Consumer pattern:
 *   ────────────────────────────────────────────────────
 *   MatchingEngine is the CONSUMER.
 *   It calls orderBook.takeBestMatch() which blocks on ordersAvailable.await()
 *   when no match exists, and returns null when exchange is closed.
 *   This is exactly the Condition-based producer-consumer from the lecture.
 *
 *   Shutdown via AtomicBoolean:
 *   ────────────────────────────
 *   When main sets exchangeOpen=false and calls wakeAll(), the next
 *   takeBestMatch() call returns null → engine exits its loop cleanly.
 */
public class MatchingEngine implements Callable<String> {

    private final OrderBook     orderBook;
    private final ExchangeStats stats;
    private final AtomicBoolean exchangeOpen;

    public MatchingEngine(OrderBook orderBook, ExchangeStats stats,
                          AtomicBoolean exchangeOpen) {
        this.orderBook    = orderBook;
        this.stats        = stats;
        this.exchangeOpen = exchangeOpen;
    }

    /**
     * Main loop: pull matches from OrderBook, record each in ExchangeStats.
     *
     * takeBestMatch() blocks on ordersAvailable Condition when no match exists.
     * It returns null when exchangeOpen=false AND no match remains → we break.
     *
     * After breaking, we return a summary String — captured by Future<String> in Task 5.
     */
    @Override
    public String call() throws Exception {
        int matchedTrades = 0;
        System.out.println("[Engine] Started.");

        while (true) {
            MatchedPair match = orderBook.takeBestMatch(exchangeOpen);

            if (match == null) {
                break;   // exchange closed, no more matches possible
            }

            // Record trade in stats:
            //   AtomicInteger.incrementAndGet() for totalTrades (lock-free)
            //   WriteLock for price fields (consistent snapshot)
            stats.recordTrade(match.getExecutionPrice(), match.getExecutedQuantity());

            System.out.printf("[Engine]   MATCHED: %s%n", match);
            matchedTrades++;
        }

        System.out.println("[Engine] Stopped.");
        return "Engine executed " + matchedTrades + " trades.";
    }
}