package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.and.orderbook.OrderBook;

import java.util.Objects;
import java.util.Optional;

class LowestPriceOfSellAction extends AbstractAction {

    LowestPriceOfSellAction(OrderBook orderBook) {
        super(orderBook);
    }

    @Override
    public void execute() {
        Optional<Order> lowestSellOrderOpt = orderBook.getLowestSellOrder();
        if(lowestSellOrderOpt.isPresent()) {
            Order order = lowestSellOrderOpt.get();
            Integer price = order.getPrice();
            Integer size = orderBook.getSizeByPrice(price);
            LOG.info(price + "," + size);
        } else {
            LOG.info("empty");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "LowestPriceOfSellAction{}";
    }
}
