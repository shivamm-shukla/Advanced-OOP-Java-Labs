package Lab05_BehavioralDesignPatterns.ObserverPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


interface StockObserver {
    void onPriceChanged(String ticker, double oldPrice, double newPrice);
}


class StockMarket {
    private Map<String, Double> prices = new HashMap<>();
    private List<StockObserver> observers = new ArrayList<>();

    // --- Observer management ---

    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    // --- Core logic ---

    public void updatePrice(String ticker, double newPrice) {
        double oldPrice = prices.getOrDefault(ticker, 0.0);
        prices.put(ticker, newPrice);
        notifyObservers(ticker, oldPrice, newPrice);
    }

    public double getPrice(String ticker) {
        return prices.getOrDefault(ticker, 0.0);
    }

    // --- Private helpers ---

    private void notifyObservers(String ticker, double oldPrice, double newPrice) {
        for (StockObserver observer : observers) {
            try {
                observer.onPriceChanged(ticker, oldPrice, newPrice);
            } catch (Exception e) {
                // Defensive: one bad observer must not block the rest
                System.err.printf("[StockMarket] Observer %s threw an exception: %s%n",
                        observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}


class DashboardDisplay implements StockObserver {

    @Override
    public void onPriceChanged(String ticker, double oldPrice, double newPrice) {
        double percentChange = calculateChange(oldPrice, newPrice);
        System.out.printf("[DASHBOARD] %s: ₹%.2f (%+.1f%%)%n",
                ticker, newPrice, percentChange);
    }

    private double calculateChange(double old, double now) {
        if (old == 0) return 0;
        return ((now - old) / old) * 100;
    }
}


class AlertSystem implements StockObserver {
    private static final double THRESHOLD = 0.05; // 5%

    @Override
    public void onPriceChanged(String ticker, double oldPrice, double newPrice) {
        if (oldPrice == 0) return;
        if (Math.abs(newPrice - oldPrice) / oldPrice > THRESHOLD) {
            System.out.printf("[ALERT] %s moved from ₹%.2f to ₹%.2f (>5%% change)%n",
                    ticker, oldPrice, newPrice);
        }
    }
}


class TradeLogger implements StockObserver {

    @Override
    public void onPriceChanged(String ticker, double oldPrice, double newPrice) {
        long timestamp = System.currentTimeMillis(); // observer computes its own timestamp
        System.out.printf("[LOG %d] %s: ₹%.2f -> ₹%.2f%n",
                timestamp, ticker, oldPrice, newPrice);
    }
}


class MobilePushObserver implements StockObserver {
    private final String username;

    public MobilePushObserver(String username) {
        this.username = username;
    }

    @Override
    public void onPriceChanged(String ticker, double oldPrice, double newPrice) {
        double change = oldPrice == 0 ? 0 : ((newPrice - oldPrice) / oldPrice) * 100;
        System.out.printf("[PUSH → %s] %s is now ₹%.2f (%+.1f%%)%n",
                username, ticker, newPrice, change);
    }
}


// AbstractClassVersion.java
public class Main {
    public static void main(String[] args) {

        StockMarket market = new StockMarket();

        // Save reference to dashboard so we can remove it later
        DashboardDisplay dashboard = new DashboardDisplay();

        // Wire up all observers
        market.addObserver(dashboard);
        market.addObserver(new AlertSystem());
        market.addObserver(new TradeLogger());
        market.addObserver(new MobilePushObserver("rahul@example.com"));

        System.out.println(".... Price Updates .....\n");

        market.updatePrice("RELIANCE", 2450.00);
        System.out.println();

        market.updatePrice("TCS", 3200.00);
        System.out.println();

        market.updatePrice("RELIANCE", 2600.00);  // >5% change — alert fires
        System.out.println();

        market.updatePrice("INFY", 1500.00);
        System.out.println();

        market.updatePrice("TCS", 3100.00);
        System.out.println();

        // Removing the ORIGINAL dashboard reference, not a new object
        System.out.println("... Dashboard unsubscribed ...\n");
        market.removeObserver(dashboard);
        market.updatePrice("INFY", 1550.00);  // dashboard will not print now
    }
}












