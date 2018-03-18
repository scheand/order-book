package org.and.orderbook.actions;

import org.and.orderbook.OrderBook;

import java.util.Objects;

class FindSizeByPriceAction extends AbstractAction {

    private final Integer price;

    FindSizeByPriceAction(OrderBook orderBook, Integer price) {
        super(orderBook);
        Objects.requireNonNull(price);
        this.price = price;
    }

    @Override
    public void execute() {
        Integer size = orderBook.getSizeByPrice(price);
        LOG.info(size.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FindSizeByPriceAction that = (FindSizeByPriceAction) o;

        return price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return price.hashCode();
    }

    @Override
    public String toString() {
        return "FindSizeByPriceAction{" +
                "price=" + price +
                '}';
    }
}
