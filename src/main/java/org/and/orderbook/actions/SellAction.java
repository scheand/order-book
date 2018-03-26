package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

class SellAction implements Action {

    private final int size;

    public SellAction(int size) {
        if(size < 0)
            throw new IllegalArgumentException();

        this.size = size;
    }

    @Override
    public void apply(IOrderBook orderBook) {
        orderBook.sell(size);
    }
}
