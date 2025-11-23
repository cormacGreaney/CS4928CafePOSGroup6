package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObserverTests {

    @Test
    void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));

        o.addItem(new LineItem(p, 1));

        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void multiple_observers_receive_ready_event() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(2);

        List<String> events1 = new ArrayList<>();
        List<String> events2 = new ArrayList<>();
        o.register((order, evt) -> events1.add(evt));
        o.register((order, evt) -> events2.add(evt));

        o.markReady();

        assertTrue(events1.contains("ready"));
        assertTrue(events2.contains("ready"));
    }
}
