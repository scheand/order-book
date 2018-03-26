package org.and.orderbook;


import org.junit.Test;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;

public class OrderBookIntegrationTest {

    static final String PATH_TO_FIRST_TASK_FILE = "./src/test/resources/actions-file-1.txt";
    static final String PATH_TO_SECOND_TASK_FILE = "./src/test/resources/actions-file-2.txt";

    @Test
    public void testFirstTask() {
        OrderBookApplication app = new OrderBookApplication();
        IOrderBook orderBook = app.runFirstTask(PATH_TO_FIRST_TASK_FILE);


        assertEquals(valueOf(95), orderBook.bestBid());
        assertEquals(valueOf(99), orderBook.bestAsk());

        assertEquals(valueOf(20), orderBook.sizeByPrice(95));
        assertEquals(valueOf(75), orderBook.sizeByPrice(99));
        assertEquals(valueOf(300), orderBook.sizeByPrice(101));
    }

    @Test
    public void testSecondTask() {
        OrderBookApplication app = new OrderBookApplication();
        IOrderBook orderBook = app.runSecondTask(PATH_TO_SECOND_TASK_FILE);

        assertEquals(valueOf(10), orderBook.bestBid());
        assertEquals(valueOf(11), orderBook.bestAsk());

        assertEquals(valueOf(1), orderBook.sizeByPrice(9)); //bid
        assertEquals(valueOf(1), orderBook.sizeByPrice(10)); //bid
        assertEquals(valueOf(5), orderBook.sizeByPrice(11)); //sell
    }

}