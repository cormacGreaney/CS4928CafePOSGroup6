package com.cafepos.common;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderIds {
    private static final AtomicLong NEXT = new AtomicLong(1000);

    private OrderIds() {}

    public static long next() {
        return NEXT.incrementAndGet();
    }
}
