package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public final class LoyaltyPercentDiscount implements PricingService.DiscountPolicy {
    private final int percent;

    public LoyaltyPercentDiscount(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be between 0 and 100");
        }
        this.percent = percent;
    }

    @Override
    public Money calculateDiscount(Money subtotal) {
        if (percent == 0) return Money.zero();
        BigDecimal discountAmount = subtotal.asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        // Return positive discount amount
        return Money.of(discountAmount);
    }
}

