# Café POS & Delivery Project

## Week 10 Lab - Layering vs Partitioning

### Why we chose a Layered Monolith (for now)

We decided to stick with a layered monolith architecture because our Café POS system is still a single application that doesn't really need to be split up yet. The layered structure gives us good separation of concerns without adding unnecessary complexity. Since everything runs in the same process, we don't have to deal with network issues, debugging is simpler, and deployment is straightforward. 

Right now, the benefits of breaking into microservices (like independent scaling or using different technologies) don't seem worth the extra hassle. We'd have to handle distributed transactions, service discovery, and network failures, which feels like overkill for where we're at.

### Natural partitioning seams for the future

If the system grows, we can see a few places where it would make sense to split things out:

1. **Payments Service**: Payment processing has different requirements - it needs strict compliance (PCI-DSS), high availability, and could scale independently during busy periods.

2. **Notifications Service**: Sending customer notifications (SMS, email, push) doesn't need to block order processing. These can be handled asynchronously with different delivery requirements.

3. **Inventory/Catalog Service**: If we expand to multiple locations, sharing the product catalog and inventory as a separate service would make sense.

4. **Reporting/Analytics Service**: Business reports can be computationally heavy and would benefit from their own infrastructure without slowing down the main POS operations.

### Connectors/protocols for future partitioning

If we split into separate services, we'd use:

- **Events (Async)**: A distributed event bus (like Kafka or RabbitMQ) instead of our current in-process EventBus. This would handle order lifecycle events (OrderCreated, OrderPaid, OrderReady) and keep services loosely coupled.

- **REST APIs (Sync)**: For payment processing, we'd expose REST endpoints like `/payments/process` and `/payments/{id}/status` for the Payment Service.

- **Message Queues**: For notifications, we'd use queues (like SQS or RabbitMQ) to decouple notification sending from order processing, so notifications don't slow down the critical path.

The EventBus we built for this lab is basically a prototype of what a distributed event system would look like when we partition later.
