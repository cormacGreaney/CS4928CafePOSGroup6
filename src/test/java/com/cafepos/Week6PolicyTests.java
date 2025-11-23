package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Week6PolicyTests {

    @Test void loyalty_discount_5_percent() {
        // Test old interface - LoyaltyPercentDiscount implements new interface, so we test via adapter
        PricingService.DiscountPolicy newD = new LoyaltyPercentDiscount(5);
        Money discount = newD.calculateDiscount(Money.of(7.80));
        assertEquals(Money.of(0.39), discount);
    }

    @Test void fixed_rate_tax_10_percent() {
        // Test new interface - FixedRateTaxPolicy implements PricingService.TaxPolicy
        PricingService.TaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(0.74), t.calculateTax(Money.of(7.41)));
    }

    @Test void pricing_pipeline() {
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(7.80));
        assertEquals(Money.of(0.39), pr.discount());
        assertEquals(Money.of(7.41), Money.of(pr.subtotal().asBigDecimal().subtract(pr.discount().asBigDecimal())));
        assertEquals(Money.of(0.74), pr.tax());
        assertEquals(Money.of(8.15), pr.total());
    }
}