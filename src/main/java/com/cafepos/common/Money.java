package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;
    private final BigDecimal amount;

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        BigDecimal scaled = a.setScale(2, ROUND);
        if (scaled.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("negative amounts not allowed");
        }
        this.amount = scaled;
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        if (other == null) throw new IllegalArgumentException("other required");
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int qty) {
        if (qty < 0) throw new IllegalArgumentException("qty must be >= 0");
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    public Money multiply(double multiplier) {
        BigDecimal m = BigDecimal.valueOf(multiplier);
        return new Money(this.amount.multiply(m));
    }

    public Money divide(int divisor) {
        if (divisor <= 0) throw new IllegalArgumentException("divisor must be > 0");
        return new Money(this.amount.divide(BigDecimal.valueOf(divisor), 2, ROUND));
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount.setScale(2, ROUND).toPlainString();
    }
}
