package com.cafepos.demo;

import com.cafepos.common.OrderIds;
import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public final class Week5Demo {
    public static void main(String[] args) {
        ProductFactory factory = new ProductFactory();

        Product p1 = factory.create("ESP+SHOT+OAT"); // Espresso + Extra Shot + Oat Milk
        Product p2 = factory.create("LAT+L");         // Latte (Large)

        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(p1, 1));
        order.addItem(new LineItem(p2, 2));

        System.out.println("Order #" + order.id());
        for (LineItem li : order.items()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }

        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (10%): " + order.taxAtPercent(10));
        System.out.println("Total: " + order.totalWithTax());
    }
}
