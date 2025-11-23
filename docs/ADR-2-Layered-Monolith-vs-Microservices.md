# ADR-2: Why We Chose a Layered Monolith (For Now)

## The Problem

We needed to decide on the overall architecture for our Café POS system. The system handles orders, payments, notifications, inventory, and reporting. We had to choose: should we build everything as one application (monolith) or split it into separate services (microservices)?

## What We Considered

### Option 1: Microservices
Split the system into separate services - one for payments, one for orders, one for notifications, etc.

**The good parts:**
- Each service can scale independently
- Different services could use different technologies
- Teams could work on different services independently
- If one service fails, others keep running

**The bad parts:**
- Distributed transactions are complicated
- Need service discovery and load balancing
- Network calls add latency and can fail
- Much more complex to deploy and monitor
- Way too much overhead for where we're at right now

### Option 2: Layered Monolith (What We Chose)
Keep everything in one application, but organize it into clear layers.

**Why this works better for us:**
- Simple deployment - just one thing to deploy
- No network latency - everything runs in the same process
- Easier to debug and test
- Clear separation via layers (Presentation → Application → Domain ← Infrastructure)
- We can evolve to microservices later when we actually need it

**The downside:**
- All components scale together (but we don't need independent scaling yet)
- Single technology stack (but that's fine for now)
- Need to be disciplined to avoid tight coupling (but the layers help with that)

## What Happened

We built a four-layer architecture:
- **Presentation** (`ui` package) - handles user interaction
- **Application** (`app` package) - orchestrates use cases
- **Domain** (`domain` package) - core business logic (no dependencies!)
- **Infrastructure** (`infra` package) - adapters for external things

Dependencies point inward - Domain doesn't depend on anything, everything depends on Domain. This keeps our core business logic stable and independent.

We also identified natural seams where we could split later if needed:
- **Payments Service** - different compliance needs, could scale independently
- **Notifications Service** - async processing, different delivery requirements
- **Inventory/Catalog Service** - could be shared across multiple café locations
- **Reporting Service** - heavy computation, separate infrastructure

## The Future

When the system grows and we actually need independent scaling or different technologies, we can partition along these seams. We'd use REST APIs for payments, message queues for notifications, and a distributed event bus. But for now, the layered monolith is the right choice - simple, fast to develop, and easy to test.

## Where to See It

- `ARCHITECTURE.md` - Shows the full layered architecture
- `README.md` - Trade-offs section explains this decision
- `src/main/java/com/cafepos/infra/Wiring.java` - Shows how everything is wired together
