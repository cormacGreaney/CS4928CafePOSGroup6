package com.cafepos.observer;

import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;

import java.util.List;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            List<LineItem> items = order.items();
            LineItem last = items.get(items.size() - 1);

            System.out.println("[Kitchen] Order #" + order.id() + ": "
                    + last.quantity() + "x " + last.product().name() + " added");
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": payment received");
        }
    }
}
