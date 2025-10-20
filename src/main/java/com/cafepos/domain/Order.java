package com.cafepos.domain;

import com.cafepos.common.Money;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.observer.OrderObserver;
import com.cafepos.observer.OrderPublisher;

import java.util.ArrayList;
import java.util.List;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public long id() { return id; }

    public List<LineItem> items() { return items; }

    @Override
    public void register(OrderObserver o) {
        if (o == null) throw new IllegalArgumentException("observer required");
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver obs : observers) {
            obs.updated(order, eventType);
        }
    }

    public void addItem(LineItem li) {
        if (li == null) throw new IllegalArgumentException("line item required");
        items.add(li);
        notifyObservers(this, "itemAdded");
    }

    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent must be >= 0");
        Money tax = subtotal().multiply(percent).multiply(1).divide(100);
        return tax;
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        strategy.pay(this);
        notifyObservers(this, "paid");
    }

    public void markReady() {
        notifyObservers(this, "ready");
    }
}
