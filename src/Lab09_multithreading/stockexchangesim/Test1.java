package Lab09_multithreading.stockexchangesim;

public class Test1 {
    public static void main(String[] args) {
        Order buy = new Order("ORD-001", "Alice", Order.OrderType.BUY,
                "TCS", 3520.00, 15);
        Order sell = new Order("ORD-002", "Bob", Order.OrderType.SELL,
                "TCS", 3510.00, 15);
        System.out.println(buy);
        System.out.println(sell);

        MatchedPair pair = new MatchedPair(buy, sell);
        System.out.println(pair); // execution price = 3515.00

    }


}
