package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

public interface Action {

    void apply(IOrderBook orderBook);

}
