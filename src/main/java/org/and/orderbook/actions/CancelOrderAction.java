package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

import java.util.Objects;

class CancelOrderAction implements Action {

    private final Integer id;

    CancelOrderAction(Integer id) {
        Objects.requireNonNull(id, "Order ID must be specified for 'cancel' action.");
        this.id = id;
    }

    @Override
    public void apply(IOrderBook orderBook) {
        orderBook.cancelOrder(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CancelOrderAction that = (CancelOrderAction) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "CancelOrderAction{" +
                "id=" + id +
                '}';
    }
}
