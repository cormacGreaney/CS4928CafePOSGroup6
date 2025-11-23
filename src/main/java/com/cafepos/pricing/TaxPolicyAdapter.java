package com.cafepos.pricing;

import com.cafepos.common.Money;

/**
 * Adapter to make old TaxPolicy work with new PricingService.TaxPolicy
 */
public final class TaxPolicyAdapter implements PricingService.TaxPolicy {
    private final TaxPolicy oldPolicy;

    public TaxPolicyAdapter(TaxPolicy oldPolicy) {
        this.oldPolicy = oldPolicy;
    }

    @Override
    public Money calculateTax(Money subtotal) {
        return oldPolicy.taxOn(subtotal);
    }
}

