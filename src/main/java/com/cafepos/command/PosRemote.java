package com.cafepos.command;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PosRemote {
    private final Command[] slots;
    private final Deque<Command> history = new ArrayDeque<>();

    public PosRemote(int n) {
        if (n <= 0) throw new IllegalArgumentException("slots must be > 0");
        this.slots = new Command[n];
    }

    public void setSlot(int i, Command c) {
        if (i < 0 || i >= slots.length) throw new IndexOutOfBoundsException("slot " + i);
        slots[i] = c;
    }

    public void press(int i) {
        if (i < 0 || i >= slots.length) { System.out.println("[Remote] invalid slot " + i); return; }
        Command c = slots[i];
        if (c == null) {
            System.out.println("[Remote] No command in slot " + i);
            return;
        }
        c.execute();
        history.push(c);
    }

    public void undo() {
        if (history.isEmpty()) { System.out.println("[Remote] Nothing to undo"); return; }
        history.pop().undo();
    }
}
