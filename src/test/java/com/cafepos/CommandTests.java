package com.cafepos;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.OrderService;
import com.cafepos.command.PosRemote;
import com.cafepos.domain.Order;
import com.cafepos.common.OrderIds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandTests {
    @Test void add_and_undo() {
        var order = new Order(OrderIds.next());
        var service = new OrderService(order);
        var remote = new PosRemote(1);
        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT", 1));

        int before = order.items().size();
        remote.press(0);
        int afterAdd = order.items().size();
        remote.undo();
        int afterUndo = order.items().size();

        assertEquals(before + 1, afterAdd);
        assertEquals(before, afterUndo);
    }
}
