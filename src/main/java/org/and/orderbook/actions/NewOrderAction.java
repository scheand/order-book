package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;
import org.and.orderbook.Order;

import java.util.Objects;

class NewOrderAction implements Action {

    final private Order order;

    NewOrderAction(Order order) {
        Objects.requireNonNull(order, "Order must be specified for New Order Action.");
        this.order = order;
    }

    @Override
    public void apply(IOrderBook orderBook) {
        orderBook.add(order);
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewOrderAction that = (NewOrderAction) o;

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
