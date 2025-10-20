package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTests {

    @Test
    void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));

        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;

        order.pay(fake);

        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void null_strategy_throws() {
        var order = new Order(99);
        assertThrows(IllegalArgumentException.class, () -> order.pay(null));
    }

    @Test
    void cash_payment_runs() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(100);
        order.addItem(new LineItem(p, 2));

        assertDoesNotThrow(() -> order.pay(new CashPayment()));
    }

    @Test
    void card_payment_runs() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(101);
        order.addItem(new LineItem(p, 1));

        assertDoesNotThrow(() -> order.pay(new CardPayment("1234567812341234")));
    }

    @Test
    void wallet_payment_runs() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(102);
        order.addItem(new LineItem(p, 1));

        assertDoesNotThrow(() -> order.pay(new WalletPayment("alice-wallet-01")));
    }
}
