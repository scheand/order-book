package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

public class BestBidAction implements Action {

    @Override
    public void apply(IOrderBook orderBook) {
        Integer bestBidPrice = orderBook.bestBid();
        if (bestBidPrice == null) {
            System.out.println("empty");
        } else {
            Integer size = orderBook.sizeByPrice(bestBidPrice);
            System.out.println(bestBidPrice + "," + size);
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
        return "BestBidAction{}";
    }
}
