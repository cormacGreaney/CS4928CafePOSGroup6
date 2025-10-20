package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public final class PricingService {
    private final DiscountPolicy discountPolicy;
    private final TaxPolicy taxPolicy;

    public PricingService(DiscountPolicy discountPolicy, TaxPolicy taxPolicy) {
        this.discountPolicy = discountPolicy;
        this.taxPolicy = taxPolicy;
    }

    public PricingResult price(Money subtotal) {
        Money discount = discountPolicy.discountOf(subtotal);
        BigDecimal discountedBD = subtotal.asBigDecimal().subtract(discount.asBigDecimal());
        Money discounted = Money.of(discountedBD);
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();
        Money tax = taxPolicy.taxOn(discounted);
        Money total = discounted.add(tax);
        return new PricingResult(subtotal, discount, tax, total);
    }

    public static record PricingResult(Money subtotal, Money discount, Money tax, Money total) {}
}