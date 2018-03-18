package org.and.orderbook.actions;

import org.and.orderbook.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

abstract class AbstractAction implements Action {

    static final Logger LOG = LoggerFactory.getLogger(Action.class);
    protected final OrderBook orderBook;

    AbstractAction(OrderBook orderBook) {
        Objects.requireNonNull(orderBook);
        this.orderBook = orderBook;
    }

}
