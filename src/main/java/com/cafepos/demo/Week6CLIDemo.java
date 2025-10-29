package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.checkout.ReceiptPrinter;
import com.cafepos.common.Money;
import com.cafepos.common.Priced;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.common.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import com.cafepos.pricing.*;

import java.util.List;
import java.util.Scanner;

public final class Week6CLIDemo {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Café POS (Week 6) — Interactive CLI ===");

        ProductFactory factory = new ProductFactory();
        Order order = new Order(OrderIds.next());

        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        DiscountPolicy discount = new NoDiscount();
        TaxPolicy tax = new FixedRateTaxPolicy(10);
        PaymentStrategy payment = new CashPayment();
        ReceiptPrinter printer = new ReceiptPrinter();

        boolean running = true;
        while (running) {
            System.out.println("\nOrder #" + order.id());
            System.out.println("1) Add item (recipe)");
            System.out.println("2) View order");
            System.out.println("3) Remove last item");
            System.out.println("4) Choose discount");
            System.out.println("5) Choose payment");
            System.out.println("6) Checkout");
            System.out.println("7) Exit");
            System.out.print("Select option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> addItem(factory, order);
                case "2" -> viewOrder(order);
                case "3" -> removeLastItem(order);
                case "4" -> discount = chooseDiscount();
                case "5" -> payment = choosePayment();
                case "6" -> {
                    checkout(order, discount, tax, printer, payment);
                    order = new Order(OrderIds.next());
                    discount = new NoDiscount();
                    payment = new CashPayment();
                }
                case "7" -> running = false;
                default -> System.out.println("Invalid choice, try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void addItem(ProductFactory factory, Order order) {
        System.out.print("Enter recipe (e.g. ESP+SHOT+OAT or LAT+L): ");
        String recipe = sc.nextLine().trim();
        try {
            Product p = factory.create(recipe);
            System.out.print("Quantity: ");
            int qty = readInt();
            if (qty <= 0) {
                System.out.println("Quantity must be > 0");
                return;
            }
            order.addItem(new LineItem(p, qty));
            System.out.println("Added " + qty + " x " + p.name());
        } catch (Exception e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }

    private static void viewOrder(Order order) {
        List<LineItem> items = order.items();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("Current items:");
        for (LineItem li : items) {
            Money unit = (li.product() instanceof Priced p) ? p.price() : li.product().basePrice();
            System.out.println(" - " + li.product().name() + " x" + li.quantity()
                    + " = " + unit.multiply(li.quantity()));
        }
        System.out.println("Subtotal: " + order.subtotal());
    }

    private static void removeLastItem(Order order) {
        try {
            order.removeItem();
            System.out.println("Last item removed.");
        } catch (IllegalStateException e) {
            System.out.println("No items to remove.");
        }
    }

    private static DiscountPolicy chooseDiscount() {
        System.out.println("\nChoose discount:");
        System.out.println("1) None");
        System.out.println("2) Loyalty 5%");
        System.out.println("3) Coupon €1 off");
        System.out.print("Select: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> new LoyaltyPercentDiscount(5);
            case "3" -> new FixedCouponDiscount(Money.of(1.00));
            default -> new NoDiscount();
        };
    }

    private static PaymentStrategy choosePayment() {
        System.out.println("\nChoose payment method:");
        System.out.println("1) Cash");
        System.out.println("2) Card");
        System.out.println("3) Wallet");
        System.out.print("Select: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> new CardPayment("1234567812341234");
            case "3" -> new WalletPayment("user-wallet-789");
            default -> new CashPayment();
        };
    }

    private static void checkout(Order order,
                                 DiscountPolicy discount,
                                 TaxPolicy tax,
                                 ReceiptPrinter printer,
                                 PaymentStrategy payment) {

        if (order.items().isEmpty()) {
            System.out.println("No items to checkout.");
            return;
        }

        Money subtotal = order.subtotal();
        PricingService pricing = new PricingService(discount, tax);
        var pr = pricing.price(subtotal);

        int totalQty = order.items().stream().mapToInt(LineItem::quantity).sum();
        String receipt = printer.format("Cart", totalQty, pr, 10);
        System.out.println("\n" + receipt);

        order.pay(payment);
    }

    private static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
