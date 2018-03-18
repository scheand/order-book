package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.and.orderbook.OrderBook;

import java.util.Objects;

class NewOrderAction extends AbstractAction {

    final private Order order;

    NewOrderAction(OrderBook orderBook, Order order) {
        super(orderBook);
        Objects.requireNonNull(order, "Order must be specified for New Order Action.");
        this.order = order;
    }

    @Override
    public void execute() {
        orderBook.add(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewOrderAction that = (NewOrderAction) o;

        if (orderBook != null ? !orderBook.equals(that.orderBook) : that.orderBook != null) return false;
        return order.equals(that.order);
    }

    @Override
    public int hashCode() {
        return order.hashCode();
    }

    @Override
    public String toString() {
        return "NewOrderAction{" +
                "order=" + order +
                '}';
    }
}
