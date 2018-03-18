package org.and.orderbook;


import org.junit.Test;

import static org.and.orderbook.Order.Side.BUY;
import static org.and.orderbook.Order.Side.SELL;
import static org.junit.Assert.assertEquals;

public class OrderBookApplicationTest {

    static final String PATH_TO_FILE = "./src/test/resources/actions-file-1.txt";

    @Test
    public void testAppFromFile() {
        OrderBookApplication app = new OrderBookApplication();
        OrderBook orderBook = app.run(PATH_TO_FILE);

        assertEquals(new Order(0, BUY, 95, 20), orderBook.getHighestBuyOrder().get());
        assertEquals(new Order(4, SELL, 99, 50), orderBook.getLowestSellOrder().get());

        assertEquals(Integer.valueOf(20), orderBook.getSizeByPrice(95));
        assertEquals(Integer.valueOf(75), orderBook.getSizeByPrice(99));
        assertEquals(Integer.valueOf(300), orderBook.getSizeByPrice(101));

        assertEquals(5, orderBook.size());

    }

}