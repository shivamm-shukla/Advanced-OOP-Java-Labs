package Lab09_multithreading.stockexchangesim;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *   AtomicInteger / AtomicLong
 *   ───────────────────────────────────────────
 *   totalTrades and totalVolume are independent single values.
 *   AtomicInteger/Long use CAS (Compare-And-Swap) — a hardware instruction —
 *   to increment without any Java-level lock.
 *   This is faster than synchronized for single-variable updates.
 *
 *   ReadWriteLock
 *   ─────────────────────────
 *   lastTradePrice, highestPrice, lowestPrice are a GROUP of related values.
 *   If we read them with three separate atomic reads, we could see:
 *     highestPrice from trade #50 but lowestPrice from trade #47 — inconsistent snapshot!
 *   ReadWriteLock rules:
 *     Read lock  → many threads can hold it simultaneously (dashboard reads freely)
 *     Write lock → exclusive; blocks all readers and other writers
 *   "read lock = I'm reading, don't let writers in.
 *    write lock = I'm writing, block everyone."
 */
public class ExchangeStats {

    // Single-variable counters: AtomicInteger / AtomicLong
    // ( lock-free, CAS-based, no synchronized needed)
    private final AtomicInteger totalTrades = new AtomicInteger(0);
    private final AtomicLong    totalVolume = new AtomicLong(0L);

    // Grouped price fields: protected by ReadWriteLock
    // ( multiple readers OR one writer at a time)
    private final ReadWriteLock priceLock = new ReentrantReadWriteLock();
    private double lastTradePrice = 0.0;
    private double highestPrice   = 0.0;
    private double lowestPrice    = Double.MAX_VALUE;

    /**
     * Called by MatchingEngine after each trade.
     *
     * totalTrades/totalVolume: no lock — AtomicInteger.incrementAndGet() and
     * AtomicLong.addAndGet() are each atomic in one CAS instruction.
     *
     * Price fields: write lock — exclusive, ensures all three fields are
     * updated atomically relative to any concurrent reader.
     */
    public void recordTrade(double price, int quantity) {
        // Lock-free atomic increments
        totalTrades.incrementAndGet();
        totalVolume.addAndGet(quantity);

        // Exclusive write lock for the price group
        priceLock.writeLock().lock();
        try {
            lastTradePrice = price;
            if (price > highestPrice) highestPrice = price;
            if (price < lowestPrice)  lowestPrice  = price;
        } finally {
            priceLock.writeLock().unlock();
        }
    }

    /**
     * Called by Dashboard every 2 seconds.
     *
     * Read lock — multiple dashboard threads (or future monitoring threads)
     * can call getSnapshot() simultaneously without blocking each other.
     * Only blocked if engine is currently in a write.
     *
     * We read all three price fields inside a single read lock acquisition
     * → consistent snapshot (all from the same point in time).
     */
    public String getSnapshot() {
        priceLock.readLock().lock();
        try {
            double low = (lowestPrice == Double.MAX_VALUE) ? 0.0 : lowestPrice;
            return String.format(
                    "Trades: %d | Volume: %d | Last: ₹%.2f | High: ₹%.2f | Low: ₹%.2f",
                    totalTrades.get(), totalVolume.get(),
                    lastTradePrice, highestPrice, low);
        } finally {
            priceLock.readLock().unlock();
        }
    }

    // Getters (atomic reads, no lock needed)
    public int  getTotalTrades() { return totalTrades.get(); }
    public long getTotalVolume() { return totalVolume.get(); }
}