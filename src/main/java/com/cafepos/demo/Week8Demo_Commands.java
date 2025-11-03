package com.cafepos.demo;

import com.cafepos.domain.Order;
import com.cafepos.common.OrderIds;
import com.cafepos.payment.CardPayment;
import com.cafepos.command.*;

public final class Week8Demo_Commands {
    public static void main(String[] args) {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        PosRemote remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234567812343456"), 10));

        remote.press(0); // add espresso+shot+oat
        remote.press(1); // add large latte x2
        remote.undo();   // remove last add
        remote.press(1); // add large latte x2 again
        remote.press(2); // pay (strategy prints using Order.totalWithTax())
    }
}
