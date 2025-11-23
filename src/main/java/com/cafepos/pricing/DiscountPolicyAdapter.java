package com.cafepos.pricing;

import com.cafepos.common.Money;

/**
 * Adapter to make old DiscountPolicy work with new PricingService.DiscountPolicy
 */
public final class DiscountPolicyAdapter implements PricingService.DiscountPolicy {
    private final DiscountPolicy oldPolicy;

    public DiscountPolicyAdapter(DiscountPolicy oldPolicy) {
        this.oldPolicy = oldPolicy;
    }

    @Override
    public Money calculateDiscount(Money subtotal) {
        Money discount = oldPolicy.discountOf(subtotal);
        // Old interface returns positive discount, new expects positive (we subtract it)
        return discount;
    }
}

