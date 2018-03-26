package org.and.orderbook;

public interface IOrderBook {

    void add(Order order);

    void buy(int size);

    void sell(int size);

    void cancelOrder(Integer orderId);

    Integer bestBid();

    Integer bestAsk();

    Integer sizeByPrice(int price);

}
