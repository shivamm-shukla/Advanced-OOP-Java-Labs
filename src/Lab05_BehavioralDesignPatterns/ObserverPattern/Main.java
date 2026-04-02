package Lab05_BehavioralDesignPatterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface priceObserver {
    void notify(String ticker, double oldPrice, double newPrice);
}

class DashboardDisplay implements priceObserver {
    @Override
    public void notify(String ticker, double oldPrice, double newPrice) {
        refreshTicker(ticker, oldPrice, calculateChange(oldPrice, newPrice));
    }
    private void refreshTicker(String ticker, double oldPrice, double newPrice) {
        System.out.println(ticker + " - " + oldPrice + " - " + newPrice);
    }
    private double calculateChange(double oldPrice, double newPrice) {
        if (oldPrice == 0) return 0;
        return ((newPrice - oldPrice) / oldPrice)*100;

    }

}

class AlertSystemDisplay implements priceObserver {
    @Override
    public void notify(String ticker, double oldPrice, double newPrice) {
    if (Math.abs(newPrice - oldPrice) / oldPrice > 0.05) {
        sendThresholdAlert(ticker, oldPrice, newPrice);
    }
    }
    private void sendThresholdAlert(String ticker, double oldPrice, double newPrice) {
        System.out.println("Alert for " + ticker + ": " + oldPrice + " - " + newPrice);
    }
}

class TradeLogger implements priceObserver {
    @Override
    public void notify(String ticker, double oldPrice, double newPrice) {
    logPriceChange(ticker,oldPrice, newPrice, System.currentTimeMillis());
    }
    private void logPriceChange(String ticker, double oldPrice, double newPrice, long timestamp) {
        System.out.println(ticker + " - " + oldPrice + " - " + newPrice);
    }
}



class StockMarket {
    private Map<String, Double> prices = new HashMap<>();
    private List<priceObserver> observers = new ArrayList<>();
    public void updatePrice(String ticker, double newPrice){
        double oldPrice = prices.getOrDefault(ticker, 0.0);
        prices.put(ticker, newPrice);

        for (priceObserver observer : observers) {
            observer.notify(ticker, oldPrice, newPrice);
        }

    }
    public double getPrice(String ticker){
        return prices.getOrDefault(ticker, 0.0);
    }
}

public class Main {
    public static void main(String[] args) {
        StockMarket market = new StockMarket();

        market.updatePrice("BMW", 100.0);
        market.updatePrice("TCS", 2600.00);
        market.updatePrice("TCS", 20000.00);
    }
}