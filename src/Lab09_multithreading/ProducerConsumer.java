package Lab09_multithreading;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SharedBuffer {
    private final Queue<Integer> buffer = new ArrayDeque<>();
    private final int capacity;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    public void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                System.out.println(Thread.currentThread().getName() +
                        " waiting (Buffer FULL)");
                notFull.await();
            }

            buffer.add(item);
            System.out.println(Thread.currentThread().getName() +
                    " PRODUCED: " + item +
                    " | Buffer size: " + buffer.size());

            notEmpty.signalAll(); // for multiple consumers

        } finally {
            lock.unlock();
        }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                System.out.println(Thread.currentThread().getName() +
                        " waiting (Buffer EMPTY)");
                notEmpty.await();
            }

            int item = buffer.poll();
            System.out.println(Thread.currentThread().getName() +
                    " CONSUMED: " + item +
                    " | Buffer size: " + buffer.size());

            notFull.signalAll(); // for multiple producers
            return item;

        } finally {
            lock.unlock();
        }
    }
}

public class ProducerConsumer {

    public static void main(String[] args) {

        SharedBuffer buffer = new SharedBuffer(3);

        // MULTIPLE PRODUCERS
        Runnable producerTask = () -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // MULTIPLE CONSUMERS
        Runnable consumerTask = () -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // multiple threads
        Thread p1 = new Thread(producerTask, "Producer-1");
        Thread p2 = new Thread(producerTask, "Producer-2");

        Thread c1 = new Thread(consumerTask, "Consumer-1");
        Thread c2 = new Thread(consumerTask, "Consumer-2");

        // Start all
        p1.start();
        p2.start();
        c1.start();
        c2.start();
    }
}