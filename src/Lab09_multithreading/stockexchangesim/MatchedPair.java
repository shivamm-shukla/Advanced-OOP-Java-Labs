package Lab09_multithreading.stockexchangesim;

public class MatchedPair {
    private final Order buyOrder;
    private final Order sellOrder;
    private final double executionPrice;
    private final int executedQuantity;

    public MatchedPair(Order buyOrder, Order sellOrder) {
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.executionPrice = (buyOrder.getPrice() + sellOrder.getPrice()) / 2.0 ;
        this.executedQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
    }

    public Order getBuyOrder() {
        return buyOrder;
    }
    public Order getSellOrder() {
        return sellOrder;
    }
    public double getExecutionPrice() {
        return executionPrice;
    }
    public int getExecutedQuantity() {
        return executedQuantity;
    }

    @Override
    public String toString() {
        return String.format(
                "BUY %s @%.2f x%d <-> SELL %s @%.2f x%d => Executed @%.2f x%d",
                buyOrder.getStock(),
                buyOrder.getPrice(),
                executedQuantity,
                sellOrder.getStock(),
                sellOrder.getPrice(),
                executedQuantity,
                executionPrice,
                executedQuantity
        );
    }
}
