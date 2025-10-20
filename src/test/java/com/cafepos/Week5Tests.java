package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.decorator.*;
import com.cafepos.domain.*;
import com.cafepos.factory.*;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Week5Tests {

    @Test
    void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);

        assertEquals("Espresso + Extra Shot", withShot.name());
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }

    @Test
    void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));

        assertEquals("Espresso + Extra Shot + Oat Milk (Large)", decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }

    @Test
    void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");

        assertTrue(p.name().contains("Espresso"));
        assertTrue(p.name().contains("Oat Milk"));
        assertEquals(Money.of(3.80), ((Priced) p).price());
    }

    @Test
    void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso); // 3.30
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));

        assertEquals(Money.of(6.60), o.subtotal());
    }

    @Test
    void factory_and_manual_build_equivalence() {
        ProductFactory f = new ProductFactory();
        Product viaFactory = f.create("ESP+SHOT+OAT+L");
        Product viaManual = new SizeLarge(new OatMilk(new ExtraShot(
                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)))
        ));

        assertEquals(viaManual.name(), viaFactory.name());
        assertEquals(((Priced) viaManual).price(), ((Priced) viaFactory).price());

        Order o1 = new Order(1);
        o1.addItem(new LineItem(viaFactory, 1));
        Order o2 = new Order(2);
        o2.addItem(new LineItem(viaManual, 1));

        assertEquals(o1.subtotal(), o2.subtotal());
        assertEquals(o1.totalWithTax(10), o2.totalWithTax(10));
    }

    @Test
    void invalid_recipe_throws() {
        ProductFactory f = new ProductFactory();
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> f.create("INVALID"));
        assertTrue(ex.getMessage().contains("Unknown base"));
    }

    @Test
    void null_recipe_throws() {
        ProductFactory f = new ProductFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create(null));
    }

    @Test
    void empty_recipe_throws() {
        ProductFactory f = new ProductFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create(" "));
    }
}
