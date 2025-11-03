package com.cafepos.command;

public final class MarkReadyCommand implements Command {
    private final OrderService service;
    public MarkReadyCommand(OrderService service) { this.service = service; }
    @Override public void execute() { service.markReady(); }
}
