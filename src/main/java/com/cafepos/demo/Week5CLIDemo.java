package com.cafepos.demo;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import com.cafepos.observer.*;
import com.cafepos.factory.*;

import java.util.Scanner;

public final class Week5CLIDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductFactory factory = new ProductFactory();

        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        System.out.println("=== Café POS System (Week 5: Decorator + Factory) ===");
        System.out.println("Order #" + order.id() + " created.");

        boolean running = true;
        boolean paid = false;
        int taxPct = 10;

        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Add Drink via Recipe");
            System.out.println("2. View Items");
            System.out.println("3. Pay Order");
            System.out.println("4. Mark Ready");
            System.out.println("5. Save & View Receipt");
            System.out.println("6. Remove Most Recent Item");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    System.out.println("\nEnter recipe code (examples):");
                    System.out.println(" - ESP → Espresso");
                    System.out.println(" - LAT+L → Large Latte");
                    System.out.println(" - ESP+SHOT+OAT → Espresso + Extra Shot + Oat Milk");
                    System.out.println("Add-ons: SHOT, OAT, SYP, L");
                    System.out.print("Recipe: ");
                    String recipe = scanner.nextLine().trim();

                    try {
                        Product product = factory.create(recipe);
                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        order.addItem(new LineItem(product, qty));
                        System.out.println(qty + "x " + product.name() + " added (" + ((Priced) product).price() + " each).");
                    } catch (Exception e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }

                case "2" -> {
                    if (order.items().isEmpty()) {
                        System.out.println("No items yet.");
                    } else {
                        printOrderSummary(order, taxPct);
                    }
                }

                case "3" -> {
                    if (order.items().isEmpty()) {
                        System.out.println("Cannot pay: order is empty.");
                        break;
                    }

                    System.out.println("\nSelect payment method:");
                    System.out.println("1. Cash");
                    System.out.println("2. Card");
                    System.out.println("3. Wallet");
                    System.out.print("Enter choice: ");
                    String payChoice = scanner.nextLine().trim();

                    switch (payChoice) {
                        case "1" -> {
                            order.pay(new CashPayment());
                            paid = true;
                        }
                        case "2" -> {
                            System.out.print("Enter card number: ");
                            String card = scanner.nextLine().trim();
                            order.pay(new CardPayment(card));
                            paid = true;
                        }
                        case "3" -> {
                            System.out.print("Enter wallet ID: ");
                            String wallet = scanner.nextLine().trim();
                            order.pay(new WalletPayment(wallet));
                            paid = true;
                        }
                        default -> System.out.println("Invalid payment option.");
                    }
                }

                case "4" -> {
                    order.markReady();
                    System.out.println("Order marked as ready.");
                }

                case "5" -> {
                    printReceipt(order, taxPct, paid);
                }

                case "6" -> {
                    if (order.items().isEmpty()) {
                        System.out.println("Cannot remove: order is empty.");
                        break;
                    }
                    order.removeItem();
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

    private static void printOrderSummary(Order order, int taxPct) {
        System.out.println("\nOrder #" + order.id() + " items:");
        for (LineItem li : order.items()) {
            System.out.println(" - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (" + taxPct + "%): " + order.taxAtPercent(taxPct));
        System.out.println("Total: " + order.totalWithTax());
    }

    private static void printReceipt(Order order, int taxPct, boolean paid) {
        System.out.println("\n======= Café POS Receipt =======");
        System.out.println("Order #" + order.id());
        System.out.println("--------------------------------");
        for (LineItem li : order.items()) {
            System.out.println(li.product().name());
            System.out.println("   " + li.quantity() + " x " + ((Priced) li.product()).price() + " = " + li.lineTotal());
        }
        System.out.println("--------------------------------");
        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (" + taxPct + "%): " + order.taxAtPercent(taxPct));
        System.out.println("TOTAL: " + order.totalWithTax());
        System.out.println("--------------------------------");
        if (paid) {
            System.out.println("Payment: ✅ Received");
        } else {
            System.out.println("Payment: ❌ Pending");
        }
        System.out.println("Status: Ready for pickup? " + (paid ? "Awaiting confirmation" : "Not paid yet"));
        System.out.println("================================");
    }
}
