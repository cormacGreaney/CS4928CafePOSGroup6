# ADR-3: Why We Use the Repository Pattern

## The Problem

We needed a way to save and retrieve Order entities. But we didn't want the Domain layer (our core business logic) or the Application layer to know about databases, file systems, or any specific storage mechanism. That would create tight coupling and make testing harder.

## What We Considered

### Option 1: Direct Database Access in Domain Layer
Just have the Order class save itself directly to a database.

**Why we didn't choose this:**
- Domain layer would depend on infrastructure (database libraries)
- Violates our layered architecture - Domain should be independent
- Hard to test without a real database
- Can't easily swap storage mechanisms

### Option 2: Active Record Pattern
Have entities know how to save themselves, but abstract it a bit.

**Why we didn't choose this:**
- Still couples Domain entities to infrastructure
- Violates Single Responsibility Principle - an Order shouldn't know how to persist itself
- Still hard to test

### Option 3: Repository Pattern (What We Chose)
Define an interface in the Domain layer that says "here's what we need" (this is called a "port"). Then implement it in the Infrastructure layer (this is called an "adapter").

**Why this works better:**
- Domain layer defines the interface - it says what it needs, not how
- Infrastructure provides the implementation - it knows about databases/files/etc.
- Easy to swap implementations - in-memory for tests, database for production
- Domain and Application layers stay testable without needing a real database
- Clear separation: Domain defines what, Infrastructure provides how

**The downside:**
- Slight indirection (but worth it for the benefits)
- Need to manage repository lifecycle (we handle this in the Wiring class)

## What Happened

We have `OrderRepository` as an interface in the Domain layer. It just says "save an order" and "find an order by ID". The `InMemoryOrderRepository` in Infrastructure implements this using a HashMap - perfect for testing. Later, we could add a `DatabaseOrderRepository` without changing any Domain or Application code.

The Application layer (like `CheckoutService`) uses the repository interface, not a concrete implementation. This keeps everything decoupled and testable.

## Where to See It

- `src/main/java/com/cafepos/domain/OrderRepository.java` - The interface (port)
- `src/main/java/com/cafepos/infra/InMemoryOrderRepository.java` - The implementation (adapter)
- `src/main/java/com/cafepos/app/CheckoutService.java` - Uses the repository
- `src/main/java/com/cafepos/infra/Wiring.java` - Wires it all together
