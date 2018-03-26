package org.and.orderbook.actions;

import org.and.orderbook.IOrderBook;

import java.util.Objects;

class FindSizeByPriceAction implements Action {

    private final Integer price;

    FindSizeByPriceAction(Integer price) {
        Objects.requireNonNull(price);
        this.price = price;
    }

    @Override
    public void apply(IOrderBook orderBook) {
        Integer size = orderBook.sizeByPrice(price);
        System.out.println(size);
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
