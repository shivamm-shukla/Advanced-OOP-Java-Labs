package Lab05_BehavioralDesignPatterns.StrategyPattern;

interface DeliveryPricingStrategy {
    double calculateFee(double distanceKm, boolean isPeakHour, int scheduledHour);
}
 class StandardPricing implements DeliveryPricingStrategy {
     private static final double BASE_FEE     = 20.0;
     private static final double PER_KM_RATE  = 8.0;
     private static final double PEAK_MULT    = 1.5;
     private static final double LOYALTY_DISC = 30.0;
     private static final double LOYALTY_KM   = 10.0;

     @Override
     public double calculateFee(double distanceKm, boolean isPeakHour, int scheduledHour) {
         double fee = BASE_FEE + (distanceKm * PER_KM_RATE);
         if (isPeakHour) {
             fee *= PEAK_MULT;
         }
         if (distanceKm > LOYALTY_KM) {
             fee = Math.max(fee - LOYALTY_DISC, BASE_FEE);
         }
         return fee;
     }
 }

 class ExpressPricing implements DeliveryPricingStrategy {
     private static final double BASE_FEE        = 20.0;
     private static final double EXPRESS_PREMIUM  = 50.0;
     private static final double PER_KM_RATE      = 12.0;
     private static final double PEAK_SURGE       = 2.5;
     private static final double OFF_PEAK_SURGE   = 1.8;
     private static final double MAX_FEE          = 500.0;

     @Override
     public double calculateFee(double distanceKm, boolean isPeakHour, int scheduledHour) {
         double surgeMultiplier = isPeakHour ? PEAK_SURGE : OFF_PEAK_SURGE;
         double fee = BASE_FEE + EXPRESS_PREMIUM + (distanceKm * PER_KM_RATE * surgeMultiplier);
         return Math.min(fee, MAX_FEE);
     }
 }

// ScheduledPricing
class ScheduledPricing implements DeliveryPricingStrategy {
    private static final double BASE_FEE    = 20.0;
    private static final double PER_KM_RATE = 6.0;

    @Override
    public double calculateFee(double distanceKm, boolean isPeakHour, int scheduledHour) {
        double fee = BASE_FEE + (distanceKm * PER_KM_RATE);
        if (scheduledHour >= 12 && scheduledHour <= 13) {
            fee *= 1.3;  // Lunch premium
        } else if (scheduledHour >= 19 && scheduledHour <= 21) {
            fee *= 1.4;  // Dinner premium
        } else if (scheduledHour >= 22 || scheduledHour <= 6) {
            fee *= 0.7;  // Late-night discount
        }
        return fee;
    }
}

// DeliveryPriceCalculator.java
class DeliveryPriceCalculator {
    private static final double GST_RATE = 1.18;

    private DeliveryPricingStrategy strategy;


    public DeliveryPriceCalculator(DeliveryPricingStrategy strategy) {
        this.strategy = strategy;
    }

    // Setter injection — allows runtime swapping (Task 6)
    public void setStrategy(DeliveryPricingStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateDeliveryFee(double distanceKm, boolean isPeakHour, int scheduledHour) {
        double baseFee = strategy.calculateFee(distanceKm, isPeakHour, scheduledHour);
        double feeWithTax = baseFee * GST_RATE;
        return Math.round(feeWithTax * 100.0) / 100.0;
    }

    public void printReceipt(double distanceKm, boolean isPeakHour, int scheduledHour) {
        double fee = calculateDeliveryFee(distanceKm, isPeakHour, scheduledHour);
        System.out.printf("[%s] %.1f km | Peak: %s | Fee: ₹%.2f%n",
                strategy.getClass().getSimpleName(),
                distanceKm,
                isPeakHour ? "Yes" : "No",
                fee);
    }
}
// SubscriptionPricing
class SubscriptionPricing implements DeliveryPricingStrategy {
    private static final double FLAT_FEE = 25.0;

    @Override
    public double calculateFee(double distanceKm, boolean isPeakHour, int scheduledHour) {
        return FLAT_FEE;
    }
}

public class Main {
    public static void main(String[] args) {

        DeliveryPriceCalculator calc = new DeliveryPriceCalculator(new StandardPricing());

        // Standard deliveries
        System.out.println("---- Standard Deliveries ----");
        calc.printReceipt(5.0, false, 14);
        calc.printReceipt(12.0, true, 13);  // isPeakHour = true, scheduledHour = 13

        // Express deliveries
        System.out.println("\n---- Express Deliveries ----");
        calc.setStrategy(new ExpressPricing());
        calc.printReceipt(3.0, true, 20);
        calc.printReceipt(8.0, false, 10);

        // Scheduled deliveries
        System.out.println("\n---- Scheduled Deliveries ----");
        calc.setStrategy(new ScheduledPricing());
        calc.printReceipt(4.0, false, 19);
        calc.printReceipt(6.0, false, 23);

        // Subscription delivery (new — added without changing any existing class)
        System.out.println("\n---- Subscription Delivery ----");
        calc.setStrategy(new SubscriptionPricing());
        calc.printReceipt(2.0, true, 13);
        calc.printReceipt(20.0, false, 22);

        // Runtime strategy switching demo
        System.out.println("\n---- Strategy Switching at Runtime ----");
        calc.setStrategy(new StandardPricing());
        calc.printReceipt(7.0, false, 15);

        calc.setStrategy(new ExpressPricing());
        calc.printReceipt(7.0, false, 15);

        calc.setStrategy(new SubscriptionPricing());
        calc.printReceipt(7.0, false, 15);
    }
}







