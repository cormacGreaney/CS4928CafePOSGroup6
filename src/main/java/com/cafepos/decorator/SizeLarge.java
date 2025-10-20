package com.cafepos.decorator;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.common.Priced;

public final class SizeLarge extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.70);

    public SizeLarge(Product base) {
        super(base);
    }

    @Override
    public String name() {
        return base.name() + " (Large)";
    }

    @Override
    public Money price() {
        Money basePrice = (base instanceof Priced p) ? p.price() : base.basePrice();
        return basePrice.add(SURCHARGE);
    }
}
