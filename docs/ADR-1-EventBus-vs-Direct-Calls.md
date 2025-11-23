# ADR-1: Why We Chose EventBus Over Direct Calls

## The Problem

We needed a way for different parts of our system to know when things happen - like when an order is created or paid. The OrderController handles these actions, but other parts (like UI notifications, logging, or analytics) also need to react to these events.

If we used direct method calls, the OrderController would need to know about every single thing that wants to be notified. That creates tight coupling and makes the code harder to change.

## What We Considered

### Option 1: Direct Method Calls
We could have the OrderController just call methods directly on all the things that need to know.

**Why we didn't choose this:**
- The OrderController would need to know about every subscriber
- Adding a new subscriber means changing the OrderController
- Violates the Single Responsibility Principle - the controller shouldn't manage all these notifications

### Option 2: Observer Pattern in Domain Layer
We already use Observer pattern for the Order lifecycle. We could extend that.

**Why we didn't choose this:**
- The Domain layer would need to know about UI concerns
- That violates our layered architecture - Domain should be independent

### Option 3: EventBus (What We Chose)
We put an EventBus in the Application layer. Components publish events, and other components subscribe to the events they care about.

**Why this works better:**
- Loose coupling - publishers don't know who's listening
- Easy to add new subscribers without changing existing code
- Clear separation - events flow through the Application layer
- We can extend this to a distributed event bus later (like Kafka) if we need to

**The downside:**
- Slight performance overhead (but it's negligible for in-process calls)
- Events are synchronous in our current implementation

## What Happened

Components can now subscribe to events independently. We can add logging, analytics, or notifications without touching the OrderController. The event flow is clear: OrderController publishes events → EventBus → Subscribers handle them.

This also gives us a foundation for future distributed architecture - if we split into microservices later, we can replace this in-process EventBus with something like Kafka or RabbitMQ.

## Where to See It

- `src/main/java/com/cafepos/app/events/EventBus.java` - The EventBus implementation
- `src/main/java/com/cafepos/ui/EventWiringDemo.java` - Example of using it
- Events are defined as `OrderEvent` with `OrderCreated` and `OrderPaid` records
