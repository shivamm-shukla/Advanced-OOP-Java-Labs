package Lab07_Refactoring.eCommerce;

import java.util.*;

// STRATEGY
interface DiscountStrategy {
    double calculateDiscount(double price);
}

class FlatDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price) {
        double finalPrice = price - 50;
        System.out.println("[DISCOUNT] Flat ₹50 off: ₹" + finalPrice);
        return finalPrice;
    }
}

class PercentDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price) {
        double finalPrice = price * 0.85;
        System.out.println("[DISCOUNT] 15% off: ₹" + finalPrice);
        return finalPrice;
    }
}

class BogoDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price) {
        double finalPrice = price * 0.75;
        System.out.println("[DISCOUNT] BOGO: ₹" + finalPrice);
        return finalPrice;
    }
}

class NoDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price) {
        System.out.println("[DISCOUNT] None: ₹" + price);
        return price;
    }
}

//  PAYMENT (ADAPTER)

interface PaymentGateway {
    void charge(String orderId, double amount);
}

class RazorpayGateway implements PaymentGateway {
    @Override
    public void charge(String orderId, double amount) {
        System.out.println("[PAYMENT] Razorpay: Creating order...");
        System.out.println("[PAYMENT] Razorpay: Charging ₹" + amount);
        System.out.println("[PAYMENT] Razorpay: Payment ID: rzp_" + orderId);
    }
}

class PaytmGateway implements PaymentGateway {
    @Override
    public void charge(String orderId, double amount) {
        System.out.println("[PAYMENT] PayTM: Initiating txn...");
        System.out.println("[PAYMENT] PayTM: Amount: ₹" + amount);
        System.out.println("[PAYMENT] PayTM: TxnID: ptm_" + orderId);
    }
}

// Third-party (CANNOT MODIFY)
class LegacyCodGateway {
    public void confirmOrder(String orderId, double amount) {
        System.out.println("[COD] Confirmed: " + orderId + " for ₹" + amount);
        System.out.println("[COD] Collect on delivery.");
    }

    public String getReceiptNumber(String orderId) {
        return "COD-" + orderId;
    }
}

// Adapter
class CodGatewayAdapter implements PaymentGateway {
    private LegacyCodGateway legacy;

    public CodGatewayAdapter() {
        this.legacy = new LegacyCodGateway();
    }

    @Override
    public void charge(String orderId, double amount) {
        System.out.println("[PAYMENT] COD Legacy:");
        legacy.confirmOrder(orderId, amount);
    }
}

// OBSERVER

interface OrderObserver {
    void onOrderPlaced(String orderId, List<String> items, double finalPrice);
}

class KitchenObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(String orderId, List<String> items, double finalPrice) {
        System.out.println("[NOTIFY] Kitchen: Prepare "
                + items.size() + " items for order " + orderId);
    }
}

class DeliveryObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(String orderId, List<String> items, double finalPrice) {
        System.out.println("[NOTIFY] Delivery: Pickup from restaurant for order " + orderId);
    }
}

class SmsObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(String orderId, List<String> items, double finalPrice) {
        System.out.println("[NOTIFY] SMS: Your order "
                + orderId + " is confirmed! Total: ₹" + finalPrice);
    }
}

class AnalyticsObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(String orderId, List<String> items, double finalPrice) {
        System.out.println("[NOTIFY] Analytics: Order "
                + orderId + " | ₹" + finalPrice
                + " | items: " + items.size());
    }
}

// New observer (no change in existing code)
class PushNotificationObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(String orderId, List<String> items, double finalPrice) {
        System.out.println("[NOTIFY] Push: Order " + orderId + " placed successfully!");
    }
}

// ORDER PROCESSOR

class OrderProcessor {
    private DiscountStrategy discountStrategy;
    private PaymentGateway paymentGateway;
    private List<OrderObserver> observers = new ArrayList<>();

    public OrderProcessor(DiscountStrategy discountStrategy,
                          PaymentGateway paymentGateway) {
        this.discountStrategy = discountStrategy;
        this.paymentGateway = paymentGateway;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    public void processOrder(String orderId, List<String> items, double subtotal) {

        System.out.println("\n=== Processing Order: " + orderId + " ===");

        // Step 1: Discount
        double finalPrice = discountStrategy.calculateDiscount(subtotal);

        // Step 2: Payment
        paymentGateway.charge(orderId, finalPrice);

        // Step 3: Notify all
        for (OrderObserver obs : observers) {
            try {
                obs.onOrderPlaced(orderId, items, finalPrice);
            } catch (Exception e) {
                System.out.println("[ERROR] Observer failed: " + e.getMessage());
            }
        }

        System.out.println("=== Order " + orderId + " complete ===\n");
    }
}

// MAIN

public class Main {
    public static void main(String[] args) {

        // ORDER 1 -> PercentDiscount + Razorpay
        OrderProcessor order1 = new OrderProcessor(
                new PercentDiscount(),
                new RazorpayGateway()
        );
        addCommonObservers(order1);
        order1.addObserver(new PushNotificationObserver()); // new -> no existing class changed
        order1.processOrder("ORD-001",
                List.of("Paneer Tikka", "Naan", "Lassi"), 450.0);

        // ORDER 2 -> FlatDiscount + PayTM
        OrderProcessor order2 = new OrderProcessor(
                new FlatDiscount(),
                new PaytmGateway()
        );
        addCommonObservers(order2);
        order2.processOrder("ORD-002",
                List.of("Biryani", "Raita"), 350.0);

        // ORDER 3 -> BogoDiscount + COD (Adapter)
        OrderProcessor order3 = new OrderProcessor(
                new BogoDiscount(),
                new CodGatewayAdapter()
        );
        addCommonObservers(order3);
        order3.processOrder("ORD-003",
                List.of("Dosa", "Filter Coffee"), 200.0);
    }

    // Helper -> adds standard observers to any order
    private static void addCommonObservers(OrderProcessor processor) {
        processor.addObserver(new KitchenObserver());
        processor.addObserver(new DeliveryObserver());
        processor.addObserver(new SmsObserver());
        processor.addObserver(new AnalyticsObserver());
    }
}