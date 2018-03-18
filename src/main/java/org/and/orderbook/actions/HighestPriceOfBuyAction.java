package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.and.orderbook.OrderBook;

import java.util.Optional;

public class HighestPriceOfBuyAction extends AbstractAction {

    public HighestPriceOfBuyAction(OrderBook orderBook) {
        super(orderBook);
    }

    @Override
    public void execute() {
        Optional<Order> highestBuyOrderOpt  = orderBook.getHighestBuyOrder();
        if(highestBuyOrderOpt.isPresent()) {
            Order order = highestBuyOrderOpt.get();
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
        return "HighestPriceOfBuyAction{}";
    }
}
