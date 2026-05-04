package Lab09_multithreading.stockexchangesim;

public class Order {
    public enum OrderType {
        BUY,
        SELL
    }

    private final String orderId;
    private final String  traderName;
    private final OrderType type;
    private final String stock;
    private final double price;
    private final int quantity;
    private final long timestamp;

    public Order(String orderId, String traderName, OrderType type, String stock, double price, int quantity) {
        this.orderId = orderId;
        this.traderName = traderName;
        this.type = type;
        this.stock = stock;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = System.nanoTime();
    }

    public String getOrderId() {
        return orderId;
    }
    public String getTraderName() {
        return traderName;
    }

    public OrderType getType() {
        return type;
    }
    public String getStock() {
        return stock;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%-4s %-10s x%-3d @ ₹ %.2f [%s by %s]",
                type, stock, quantity, price, orderId, traderName);
    }
}
