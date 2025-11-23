package com.cafepos.pricing;

import com.cafepos.common.Money;

public final class PricingService {
    private final DiscountPolicy discountPolicy;
    private final TaxPolicy taxPolicy;

    public PricingService(DiscountPolicy discountPolicy, TaxPolicy taxPolicy) {
        this.discountPolicy = discountPolicy;
        this.taxPolicy = taxPolicy;
    }

    public PricingResult price(Money subtotal) {
        Money discount = discountPolicy.calculateDiscount(subtotal);
        Money discountedSubtotal = subtotal.subtract(discount);
        Money tax = taxPolicy.calculateTax(discountedSubtotal);
        Money total = discountedSubtotal.add(tax);
        return new PricingResult(subtotal, discount, tax, total);
    }

    public record PricingResult(Money subtotal, Money discount, Money tax, Money total) {}

    public interface DiscountPolicy {
        Money calculateDiscount(Money subtotal);
    }

    public interface TaxPolicy {
        Money calculateTax(Money subtotal);
    }
}

