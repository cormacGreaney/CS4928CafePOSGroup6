package com.cafepos;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTests {
    @Test
    void addition_and_multiplication() {
        Money a = Money.of(2.00);
        Money b = Money.of(3.00);
        assertEquals(Money.of(5.00), a.add(b));

        Money twoEsp = Money.of(2.50).multiply(2);
        assertEquals(Money.of(5.00), twoEsp);

        Money tax = Money.of(8.50).multiply(0.10);
        assertEquals(Money.of(0.85), tax);
    }

    @Test
    void no_negative_money_allowed() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1.00));
    }

    @Test
    void rounding_behaviour() {
        Money m = Money.of(2.345);
        assertEquals(Money.of(2.35), m);
    }
}
