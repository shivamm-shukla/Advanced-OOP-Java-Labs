package Lab09_multithreading.stockexchangesim;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**

 *   ReentrantLock (fair=true)
 *   ─────────────────────────
 *  new ReentrantLock(true) → FIFO order; prevents trader starvation.
 *  Always unlock in finally — if we throw inside the critical section
 *             the lock MUST still be released, otherwise every other thread blocks forever.
 *  lock.lockInterruptibly() — not used here but fair ordering is applied.
 *
 *   Two Conditions
 *   ────────────────────────────────────────────────────────────────────────
 *   With synchronized + notifyAll: ONE shared wait set.
 *     notifyAll() wakes EVERY waiting thread — most immediately go back to sleep. Wasteful.
 *   With two Conditions:
 *     ordersAvailable — only the matching engine waits here.
 *                        Traders signal it after adding an order.
 *     bookNotFull      — only traders wait here when the book is full.
 *                        Engine signals it after removing a matched pair (freeing space).
 *   signal() on each condition wakes exactly one thread of the right type.
 *
 *   Timed await
 *   ────────────────────────────────────────────────────
 *   We use ordersAvailable.await(200, TimeUnit.MILLISECONDS) instead of plain await().
 *   This lets the engine wake up periodically to check the shutdown flag (exchangeOpen),
 *   preventing a permanent hang when the exchange closes.
 */
public class OrderBook {

    // Internal state (guarded entirely by 'lock')
    private final List<Order> buyOrders  = new ArrayList<>();
    private final List<Order> sellOrders = new ArrayList<>();
    private final int capacity;

    // Lock + two Conditions
    private final ReentrantLock lock = new ReentrantLock(true); // fair = FIFO, no starvation

    // Engine waits here when no match exists.  Traders signal after adding an order.
    private final Condition ordersAvailable = lock.newCondition();

    // Traders wait here when book is full.  Engine signals after removing a matched pair.
    private final Condition bookNotFull = lock.newCondition();

    public OrderBook(int capacity) {
        this.capacity = capacity;
    }

    // addOrder called by Trader threads
    /**
     * Adds an order to the book.
     * BLOCKS if the book is at capacity — trader waits on bookNotFull.
     *
     * Condition await:
     *   await() atomically releases the lock AND suspends the thread.
     *   When signaled, the thread re-acquires the lock before returning.
     *   ALWAYS use while (not if) — spurious wakeups can occur
     */
    public void addOrder(Order order) throws InterruptedException {
        lock.lock();
        try {
            while (buyOrders.size() + sellOrders.size() >= capacity) {
                bookNotFull.await();     // book full → trader blocks, releases lock
            }

            // Add to the right list based on order type
            if (order.getType() == Order.OrderType.BUY) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }

            // Wake ONE engine thread — a new order might be the missing half of a match
            ordersAvailable.signal();   // signal() not signalAll() — only engine waits here

        } finally {
            lock.unlock();
        }
    }

    // takeBestMatch called by MatchingEngine thread
    /**
     * Finds and removes a matched buy/sell pair.
     * BLOCKS if no match currently exists — engine waits on ordersAvailable.
     * Returns null when the exchange is closing AND no match remains.
     *
     *  Condition:
     *   Instead of notifyAll() waking every thread, ordersAvailable wakes only the engine.
     *   bookNotFull wakes only waiting traders.  Precise, no wasted wakeups.
     *
     * Timed await:
     *   We use await(200ms) not plain await().
     *   Every 200ms the engine wakes, checks exchangeOpen, and loops again.
     *   This prevents an infinite sleep when traders have stopped adding orders.
     */
    public MatchedPair takeBestMatch(AtomicBoolean exchangeOpen) throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                MatchedPair match = findBestMatch();

                if (match != null) {
                    // Remove both matched orders from their lists
                    buyOrders.remove(match.getBuyOrder());
                    sellOrders.remove(match.getSellOrder());

                    // Signal that space has freed up a waiting trader may now proceed
                    bookNotFull.signal();   // only traders wait on bookNotFull
                    return match;
                }

                // No match right now:
                if (!exchangeOpen.get()) {
                    return null;   // exchange is closed and no match -> engine exits
                }

                // Wait for a new order (or wake up every 200ms to recheck shutdown flag)
                ordersAvailable.await(200, TimeUnit.MILLISECONDS);
            }
        } finally {
            lock.unlock();
        }
    }

    // findBestMatch private helper, called while lock is held
    /**
     * Scans buy and sell lists for any pair where:
     *   - same stock
     *   - buy.price >= sell.price  (buyer is willing to pay at least the seller's ask)
     *
     * Returns the first match found, or null if none.
     * This is O(n*m)
     */
    private MatchedPair findBestMatch() {
        for (Order buy : buyOrders) {
            for (Order sell : sellOrders) {
                if (buy.getStock().equals(sell.getStock())
                        && buy.getPrice() >= sell.getPrice()) {
                    return new MatchedPair(buy, sell);
                }
            }
        }
        return null;
    }

    // wakeAll — called during shutdown
    /**
     * Signals BOTH conditions so no thread is left sleeping forever.
     * Called from main after exchangeOpen is set to false.
     *
     * Without this:
     *   Engine could be stuck in ordersAvailable.await(200ms) — eventually wakes,
     *   but we call wakeAll() to make shutdown immediate.
     *   Traders waiting on bookNotFull would wake on their next 200ms cycle too.
     */
    public void wakeAll() {
        lock.lock();
        try {
            ordersAvailable.signalAll();
            bookNotFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Utility
    public int getOrderCount() {
        lock.lock();
        try {
            return buyOrders.size() + sellOrders.size();
        } finally {
            lock.unlock();
        }
    }

    public int getBuyOrderCount() {
        lock.lock();
        try { return buyOrders.size(); }
        finally { lock.unlock(); }
    }

    public int getSellOrderCount() {
        lock.lock();
        try { return sellOrders.size(); }
        finally { lock.unlock(); }
    }
}