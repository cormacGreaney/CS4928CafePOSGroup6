package com.cafepos.demo;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import com.cafepos.observer.*;

import java.util.Scanner;

public final class Week4CLIDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Setup catalog
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.00)));

        //Create order
        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        System.out.println("=== Café POS System ===");
        System.out.println("Order #" + order.id() + " created.");
        int taxPct = 10;

        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Add Item");
            System.out.println("2. View Subtotal");
            System.out.println("3. Pay Order");
            System.out.println("4. Mark Ready");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    System.out.println("\nAvailable products:");
                    catalog.findById("P-ESP").ifPresent(p -> System.out.println("1. Espresso - " + p.basePrice()));
                    catalog.findById("P-CCK").ifPresent(p -> System.out.println("2. Chocolate Cookie - " + p.basePrice()));
                    catalog.findById("P-LAT").ifPresent(p -> System.out.println("3. Latte - " + p.basePrice()));
                    System.out.print("Enter product number: ");
                    String prodChoice = scanner.nextLine().trim();

                    String prodId = switch (prodChoice) {
                        case "1" -> "P-ESP";
                        case "2" -> "P-CCK";
                        case "3" -> "P-LAT";
                        default -> null;
                    };

                    if (prodId != null) {
                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        var product = catalog.findById(prodId).orElseThrow();
                        order.addItem(new LineItem(product, qty));
                        System.out.println(qty + "x " + product.name() + " added.");
                    } else {
                        System.out.println("Invalid product selection.");
                    }
                }
                case "2" -> {
                    System.out.println("Subtotal: " + order.subtotal());
                    System.out.println("Tax (" + taxPct + "%): " + order.taxAtPercent(taxPct));
                    System.out.println("Total: " + order.totalWithTax(taxPct));
                }
                case "3" -> {
                    System.out.println("\nSelect payment method:");
                    System.out.println("1. Cash");
                    System.out.println("2. Card");
                    System.out.println("3. Wallet");
                    System.out.print("Enter choice: ");
                    String payChoice = scanner.nextLine().trim();

                    switch (payChoice) {
                        case "1" -> order.pay(new CashPayment());
                        case "2" -> {
                            System.out.print("Enter card number: ");
                            String card = scanner.nextLine().trim();
                            order.pay(new CardPayment(card));
                        }
                        case "3" -> {
                            System.out.print("Enter wallet ID: ");
                            String wallet = scanner.nextLine().trim();
                            order.pay(new WalletPayment(wallet));
                        }
                        default -> System.out.println("Invalid payment option.");
                    }
                }
                case "4" -> {
                    order.markReady();
                    System.out.println("Order marked as ready.");
                }
                case "0" -> {
                    running = false;
                    System.out.println("Exiting Café POS. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}
