package com.cafepos.command;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;

public final class OrderService {
    private final ProductFactory factory = new ProductFactory();
    private final Order order;

    public OrderService(Order order) {
        if (order == null) throw new IllegalArgumentException("order required");
        this.order = order;
    }

    public void addItem(String recipe, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
        Product p = factory.create(recipe);
        order.addItem(new LineItem(p, qty));
        System.out.println("[Service] Added " + p.name() + " x" + qty);
    }

    public void removeLastItem() {
        order.removeItem();
        System.out.println("[Service] Removed last item");
    }

    public Money totalWithTax(int taxPercentIgnored) {

        return order.totalWithTax();
    }

    public void pay(PaymentStrategy strategy, int taxPercentIgnored) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        var total = order.totalWithTax();
        strategy.pay(order);
        System.out.println("[Service] Payment processed for total " + total);
    }

    public void markReady() {
        try {
            order.markReady();
            System.out.println("[Service] Order marked ready");
        } catch (NoSuchMethodError | UnsupportedOperationException e) {
            System.out.println("[Service] markReady not supported in this build");
        }
    }

    public Order order() { return order; }
}
