# Café POS System - Architecture Diagram

## Layered Architecture Overview

This diagram shows how we organized the code into four main layers. The arrows show how dependencies flow - everything points inward toward the Domain layer, which keeps the core business logic independent.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (UI)                         │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │ OrderController  │───────▶│  ConsoleView     │                      │
│  │                  │         │                  │                      │
│  │ - createOrder()  │         │ - print()        │                      │
│  │ - addItem()      │         └──────────────────┘                      │
│  │ - checkout()     │                                                   │
│  └────────┬─────────┘                                                   │
│           │                                                             │
└───────────┼─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER (Use Cases)                      │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │ CheckoutService  │───────▶│ ReceiptFormatter  │                     │
│  │                  │         │                   │                     │
│  │ - checkout()     │         │ - format()        │                     │
│  └────────┬─────────┘         └───────────────────┘                     │
│           │                                                             │
│  ┌────────┴─────────┐                                                   │
│  │   EventBus       │                                                   │
│  │   - on()         │                                                   │
│  │   - emit()       │                                                   │
│  └──────────────────┘                                                   │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │  OrderCreated    │         │   OrderPaid      │                      │
│  │  (Event)         │         │   (Event)        │                      │
│  └──────────────────┘         └──────────────────┘                      │
└───────────┬─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         DOMAIN LAYER (Core Model)                       │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │      Order       │───────▶│    LineItem      │                      │
│  │                  │         │                  │                      │
│  │ - id()           │         │ - product()      │                      │
│  │ - items()        │         │ - quantity()     │                      │
│  │ - addItem()      │         │ - lineTotal()    │                      │
│  │ - subtotal()     │         └────────┬─────────┘                      │
│  │ - taxAtPercent() │                  │                                │
│  │ - totalWithTax() │                  ▼                                │
│  └────────┬─────────┘          ┌──────────────────┐                     │
│           │                    │     Product      │                     │
│           │                    │   (from catalog) │                     │
│           │                    └──────────────────┘                     │
│           │                                                             │
│  ┌────────┴─────────┐                                                   │
│  │ OrderRepository  │                                                   │
│  │  (interface)     │                                                   │
│  │ - save()         │                                                   │
│  │ - findById()     │                                                   │
│  └──────────────────┘                                                   │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │      Money       │         │   OrderPublisher │                      │
│  │                  │         │   (Observer)     │                      │
│  │ - add()          │         └──────────────────┘                      │
│  │ - subtract()     │                                                   │
│  │ - multiply()     │                                                   │
│  └──────────────────┘                                                   │
└───────────┬─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER (Adapters)                      │
│                                                                         │
│  ┌──────────────────────┐                                               │
│  │InMemoryOrderRepository│                                              │
│  │  (implements         │                                               │
│  │   OrderRepository)   │                                               │
│  │                      │                                               │
│  │ - save()             │                                               │
│  │ - findById()         │                                               │
│  └──────────────────────┘                                               │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────┐       │
│  │                      Wiring                                  │       │
│  │  (Composition Root - Dependency Injection)                   │       │
│  │                                                              │       │
│  │  - createDefault()                                           │       │
│  │    ├─> InMemoryOrderRepository                               │       │
│  │    ├─> PricingService                                        │       │
│  │    │     ├─> LoyaltyPercentDiscount                          │       │
│  │    │     └─> FixedRateTaxPolicy                              │       │
│  │    └─> CheckoutService                                       │       │
│  └──────────────────────────────────────────────────────────────┘       │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │ PricingService   │───────▶│DiscountPolicy   │                       │
│  │                  │         │TaxPolicy         │                      │
│  │ - price()        │         │                  │                      │
│  └──────────────────┘         └──────────────────┘                      │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │LoyaltyPercent    │         │FixedRateTax      │                      │
│  │Discount          │         │Policy            │                      │
│  └──────────────────┘         └──────────────────┘                      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                         SUPPORTING PACKAGES                             │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │   ProductFactory │         │   PaymentStrategy│                      │
│  │                  │         │                  │                      │
│  │ - create()       │         │ - pay()          │                      │
│  └──────────────────┘         └──────────────────┘                      │
│                                                                         │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │   Catalog        │         │   Decorators     │                      │
│  │                  │         │   (ExtraShot,    │                      │
│  │ - findById()     │         │    OatMilk, etc) │                      │
│  └──────────────────┘         └──────────────────┘                      │
└─────────────────────────────────────────────────────────────────────────┘
```

## Component Interactions

### MVC Flow (Presentation Layer)

This shows how user input flows through the system when someone checks out:

```
User Input → OrderController → CheckoutService → OrderRepository
                                    ↓
                            ReceiptFormatter
                                    ↓
                            ConsoleView → Output
```

The controller handles the user action, the service does the business logic, and the view just prints the result. This keeps the UI code separate from the business logic.

### Event-Driven Flow (Components & Connectors)

This is how the event system works - components can publish events without knowing who's listening:

```
OrderController → EventBus.emit(OrderCreated)
                            ↓
                    Event Handlers (subscribed)
                            ↓
                    [UI] order created: {id}
```

This is useful because different parts of the system can react to events without being tightly coupled. For example, the UI can show a notification when an order is created, but the OrderController doesn't need to know about that.

### Dependency Flow

The key principle here is that dependencies always point inward:

```
Presentation → Application → Domain ← Infrastructure
     ↓              ↓            ↑            ↑
     └──────────────┴────────────┴────────────┘
              (Dependencies point inward)
```

The Domain layer (the core business logic) doesn't depend on anything - it's the most stable part. Everything else depends on it, which means we can change the UI or database without touching the core business rules.

## Key Architectural Principles

1. **Layered Architecture**: We separated the code into clear layers where each layer only depends on layers below it. This makes the code easier to understand and test.

2. **MVC Pattern**: Within the Presentation layer, we implemented a simple MVC pattern. The Model is the Domain entities (Order, LineItem), the View is ConsoleView (just handles printing), and the Controller (OrderController) coordinates everything.

3. **Ports & Adapters**: The OrderRepository interface in the Domain layer is a "port" - it defines what we need. The InMemoryOrderRepository in Infrastructure is an "adapter" - it implements that interface. Later, we could swap in a database repository without changing the Domain or Application layers.

4. **Event Bus**: The EventBus allows components to communicate without knowing about each other. Components publish events, and other components subscribe to the events they care about. This keeps things loosely coupled.

5. **Composition Root**: The Wiring class is where all the object creation happens. This makes dependencies explicit and makes it easy to swap implementations for testing or different environments.
