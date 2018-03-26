package org.and.orderbook;

import java.util.Objects;

public class Order {

    public enum Side {
        BUY,
        SELL
    }

    private Integer id;
    private Side side;
    private int price;
    private int size;


    public Order(Side side, Integer price, Integer size) {
        this(null, side, price, size);
    }

    public Order(Integer id, Side side, Integer price, Integer size) {
        //Objects.requireNonNull(id, "Order Id required");
        Objects.requireNonNull(side, "Side is required");
        if(price == null || price < 0) {
            throw new IllegalArgumentException("Price must be positive number");
        }
        if(size == null || size < 0) {
            throw new  IllegalArgumentException("Size must be positive number");
        }

        this.id = id;
        this.side = side;
        this.price = price;
        this.size = size;
    }

    public Order(Order o, Integer newSize) {
        this(o.getId(), o.getSide(), o.getPrice(), newSize);
    }

    public Integer getId() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", side=" + side +
                ", price=" + price +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!id.equals(order.id)) return false;
        if (side != order.side) return false;
        if (price != order.price) return false;
        return size == order.size;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + side.hashCode();
        result = 31 * result + price;
        result = 31 * result + size;
        return result;
    }
}
