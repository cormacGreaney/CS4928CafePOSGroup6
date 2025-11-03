package com.cafepos.command;

public final class RemoveItemCommand implements Command {
    private final OrderService service;

    public RemoveItemCommand(OrderService service) {
        this.service = service;
    }

    @Override public void execute() { service.removeLastItem(); }
    // undo for remove could be implemented if we captured the removed item; keeping simple here.
}
