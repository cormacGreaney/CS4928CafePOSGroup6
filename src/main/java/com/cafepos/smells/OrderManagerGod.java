package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public class OrderManagerGod {

    // Global/Static State: these fields are shared across all orders (not thread-safe, hard to test)
    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {
        // God Class / Long Method: This one method handles creation, pricing, discounting, tax,
        // payment I/O, and printing — violating Single Responsibility.

        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        Money unitPrice;
        try {
            // Feature Envy / Shotgun Surgery: inline type checking instead of delegating to pricing logic
            var priced = product instanceof com.cafepos.common.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        if (qty <= 0) qty = 1;

        Money subtotal = unitPrice.multiply(qty);
        Money discount = Money.zero();

        // Primitive Obsession: uses raw string discount codes instead of polymorphic strategy objects
        if (discountCode != null) {
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                // Duplicated Logic: repeated inline BigDecimal math scattered throughout
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode; // Global/Static State: records last code globally
        }

        // Duplicated Logic: direct Money/BigDecimal arithmetic repeated here
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

        // Feature Envy / Shotgun Surgery: tax computation logic inlined; any rule change edits this
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));

        var total = discounted.add(tax);

        // Primitive Obsession + God Method: paymentType is a raw string; violates Open/Closed
        // Feature Envy: payment logic belongs in separate PaymentStrategy implementations
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }

        // Duplicated / Mixed Concern: combines domain math and I/O formatting
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);

        String out = receipt.toString();
        if (printReceipt) {
            // Feature Envy: console output here instead of a ReceiptPrinter
            System.out.println(out);
        }
        return out;
    }
}
