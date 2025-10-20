package com.cafepos.decorator;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.common.Priced;

public final class Syrup extends ProductDecorator {
    private static final Money SURCHARGE = Money.of(0.40);

    public Syrup(Product base) {
        super(base);
    }

    @Override
    public String name() {
        return base.name() + " + Syrup";
    }

    @Override
    public Money price() {
        Money basePrice = (base instanceof Priced p) ? p.price() : base.basePrice();
        return basePrice.add(SURCHARGE);
    }
}
