package Lab09_multithreading.stockexchangesim;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OrderBook {

    private final List<Order> buyOrders = new ArrayList<>();
    private final List<Order> sellOrders = new ArrayList<>();
    private final int capacity;

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition ordersAvailable = lock.newCondition();
    private final Condition bookNotFull = lock.newCondition();

    public OrderBook(int capacity) {
        this.capacity = capacity;
    }

    // Add Order
    public void addOrder(Order order) throws InterruptedException {
        lock.lock();
        try {

            while (getOrderCountUnsafe() >= capacity) {
                bookNotFull.await();
            }

            if (order.getType() == Order.OrderType.BUY) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }

            ordersAvailable.signal();

        } finally {
            lock.unlock();
        }
    }

    // helpers
    public MatchedPair takeBestMatch(AtomicBoolean exchangeOpen)
            throws InterruptedException {

        lock.lock();
        try {
            while (true) {

                MatchedPair match = findBestMatch();

                if (match != null) {
                    // Remove orders
                    buyOrders.remove(match.getBuyOrder());
                    sellOrders.remove(match.getSellOrder());

                    // Notify producers (space freed)
                    bookNotFull.signal();

                    return match;
                }

                // No match found
                if (!exchangeOpen.get()) {
                    return null;
                }

                ordersAvailable.await(200, TimeUnit.MILLISECONDS);
            }

        } finally {
            lock.unlock();
        }
    }

    //  helper
    private MatchedPair findBestMatch() {

        for (Order buy : buyOrders) {
            for (Order sell : sellOrders) {

                if (buy.getStock().equals(sell.getStock()) &&
                        buy.getPrice() >= sell.getPrice()) {

                    return new MatchedPair(buy, sell);
                }
            }
        }
        return null;
    }

    public int getOrderCount() {
        lock.lock();
        try {
            return getOrderCountUnsafe();
        } finally {
            lock.unlock();
        }
    }

    private int getOrderCountUnsafe() {
        return buyOrders.size() + sellOrders.size();
    }
}