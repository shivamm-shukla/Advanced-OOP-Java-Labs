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

        System.out.println("---- Standard Pricing Tests ----");
        calc.printReceipt(5.0, false, 14);   // Normal case
        calc.printReceipt(12.0, false, 10);  // Loyalty discount (>10 km)
        calc.printReceipt(8.0, true, 18);    // Peak hour

        System.out.println("\n---- Express Pricing Tests ----");
        calc.setStrategy(new ExpressPricing());
        calc.printReceipt(3.0, false, 11);   // Off-peak express
        calc.printReceipt(10.0, true, 19);   // Peak surge
        calc.printReceipt(50.0, true, 20);   // Max cap test (should not exceed 500)

        System.out.println("\n---- Scheduled Pricing Tests ----");
        calc.setStrategy(new ScheduledPricing());
        calc.printReceipt(6.0, false, 12);   // Lunch premium
        calc.printReceipt(6.0, false, 20);   // Dinner premium
        calc.printReceipt(6.0, false, 23);   // Late-night discount
        calc.printReceipt(6.0, false, 8);    // Normal hour

        System.out.println("\n---- Subscription Pricing Tests ----");
        calc.setStrategy(new SubscriptionPricing());
        calc.printReceipt(2.0, true, 13);    // Distance/peak irrelevant
        calc.printReceipt(20.0, false, 22);  // Still flat fee

        System.out.println("\n---- Strategy Switching Test ----");
        calc.setStrategy(new StandardPricing());
        calc.printReceipt(7.0, false, 15);

        calc.setStrategy(new ExpressPricing());
        calc.printReceipt(7.0, false, 15);

        calc.setStrategy(new ScheduledPricing());
        calc.printReceipt(7.0, false, 15);
    }
}







