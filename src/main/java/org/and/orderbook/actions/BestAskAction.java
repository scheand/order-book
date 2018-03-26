package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

class BestAskAction implements Action {

    @Override
    public void apply(IOrderBook orderBook) {
        Integer bestAskPrice = orderBook.bestAsk();
        if (bestAskPrice == null) {
            System.out.println("empty");
        } else {
            Integer size = orderBook.sizeByPrice(bestAskPrice);
            System.out.println(bestAskPrice + "," + size);
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
        return "BestAskAction{}";
    }
}
