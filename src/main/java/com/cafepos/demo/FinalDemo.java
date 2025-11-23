package com.cafepos.demo;

import com.cafepos.app.events.EventBus;
import com.cafepos.app.events.OrderCreated;
import com.cafepos.app.events.OrderPaid;
import com.cafepos.checkout.ReceiptPrinter;
import com.cafepos.command.*;
import com.cafepos.infra.Wiring;
import com.cafepos.menu.*;
import com.cafepos.pricing.DiscountPolicyAdapter;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.NoDiscount;
import com.cafepos.pricing.PricingService;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import com.cafepos.state.OrderFSM;
import com.cafepos.ui.ConsoleView;
import com.cafepos.ui.OrderController;
import com.cafepos.common.Money;
import vendor.legacy.LegacyThermalPrinter;

/**
 * Final Integration Demo - Shows all patterns working together:
 * - Command Pattern (PosRemote with undo)
 * - Adapter Pattern (Legacy printer integration)
 * - Composite & Iterator (Hierarchical menu traversal)
 * - State Pattern (Order lifecycle FSM)
 * - MVC (OrderController, ConsoleView)
 * - EventBus (Event-driven communication)
 * - Layered Architecture (Presentation → Application → Domain ← Infrastructure)
 */
public final class FinalDemo {
    public static void main(String[] args) {
        System.out.println("=== Café POS Final Integration Demo ===\n");

        // 1. MVC Pattern - Layered Architecture
        System.out.println("1. MVC Pattern (Layered Architecture):");
        System.out.println("   Presentation → Application → Domain ← Infrastructure");
        var wiring = Wiring.createDefault();
        var controller = new OrderController(wiring.repo(), wiring.checkout());
        var view = new ConsoleView();
        
        long orderId = 5001L;
        controller.createOrder(orderId);
        controller.addItem(orderId, "ESP+SHOT+OAT", 1);
        controller.addItem(orderId, "LAT+L", 2);
        String receipt = controller.checkout(orderId, 10);
        view.print(receipt);
        System.out.println();

        // 2. EventBus Pattern - Components & Connectors
        System.out.println("2. EventBus Pattern (Components & Connectors):");
        EventBus bus = new EventBus();
        bus.on(OrderCreated.class, e -> System.out.println("   [Event] Order created: " + e.orderId()));
        bus.on(OrderPaid.class, e -> System.out.println("   [Event] Order paid: " + e.orderId()));
        bus.emit(new OrderCreated(orderId));
        bus.emit(new OrderPaid(orderId));
        System.out.println();

        // 3. Command Pattern - Undo/Macro support
        System.out.println("3. Command Pattern (Undo/Macro Commands):");
        var order2 = new com.cafepos.domain.Order(5002L);
        OrderService service = new OrderService(order2);
        PosRemote remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 1));
        
        System.out.println("   Pressing slot 0 (add espresso)...");
        remote.press(0);
        System.out.println("   Pressing slot 1 (add large latte)...");
        remote.press(1);
        System.out.println("   Undoing last command...");
        remote.undo();
        System.out.println("   Final items: " + order2.items().size());
        System.out.println();

        // 4. Composite & Iterator Pattern - Hierarchical menus
        System.out.println("4. Composite & Iterator Pattern (Hierarchical Menus):");
        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu("Drinks");
        Menu coffee = new Menu("Coffee");
        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        drinks.add(coffee);
        root.add(drinks);
        
        System.out.println("   Full menu structure:");
        root.print();
        System.out.println("   Vegetarian items (filtered via Iterator):");
        for (MenuItem mi : root.vegetarianItems()) {
            System.out.println("     - " + mi.name() + " = " + mi.price());
        }
        System.out.println();

        // 5. State Pattern - Order lifecycle
        System.out.println("5. State Pattern (Order Lifecycle FSM):");
        OrderFSM fsm = new OrderFSM();
        System.out.println("   Initial state: " + fsm.status());
        fsm.pay();
        System.out.println("   After payment: " + fsm.status());
        fsm.markReady();
        System.out.println("   After ready: " + fsm.status());
        fsm.deliver();
        System.out.println("   Final state: " + fsm.status());
        System.out.println();

        // 6. Adapter Pattern - Legacy integration
        System.out.println("6. Adapter Pattern (Legacy Printer Integration):");
        var pricing = new PricingService(
            new DiscountPolicyAdapter(new NoDiscount()),
            new FixedRateTaxPolicy(10)
        );
        var pr = pricing.price(Money.of(7.80));
        var receiptText = new ReceiptPrinter().format("LAT+L", 2, pr, 10);
        
        // Adapt legacy printer to our Printer interface
        LegacyThermalPrinter legacyPrinter = new LegacyThermalPrinter();
        Printer printer = new LegacyPrinterAdapter(legacyPrinter);
        System.out.println("   Adapting LegacyThermalPrinter to Printer interface...");
        printer.print(receiptText);
        System.out.println("   Adapter allows integration without modifying legacy code");
        System.out.println();

        System.out.println("=== All patterns integrated successfully! ===");
    }
}





