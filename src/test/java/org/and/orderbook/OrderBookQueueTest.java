package org.and.orderbook;


import org.junit.Before;
import org.junit.Test;

import static org.and.orderbook.Order.Side.BUY;
import static org.and.orderbook.Order.Side.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderBookQueueTest {


    OrderBookQueue orderBook;

    @Before
    public void setUp() {
        orderBook = new OrderBookQueue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenTryingToAddDuplicateByIdOrder() {
        orderBook.add(new Order(1, BUY, 50, 100));
        orderBook.add(new Order(1, BUY, 50, 100));
    }

    @Test
    public void testHighestBuyOrder() {
        orderBook.add(new Order(1, BUY, 50, 100));
        assertEquals(new Order(1, BUY, 50, 100), orderBook.getHighestBuyOrder().get());
        assertFalse(orderBook.getLowestSellOrder().isPresent());

        orderBook.add(new Order(2, BUY, 55, 100));
        assertEquals(new Order(2, BUY, 55, 100), orderBook.getHighestBuyOrder().get());
        assertFalse(orderBook.getLowestSellOrder().isPresent());
    }

    @Test
    public void testLowestSellOrder() {
        orderBook.add(new Order(5, SELL, 50, 50));
        assertFalse(orderBook.getHighestBuyOrder().isPresent());
        assertEquals(new Order(5, SELL, 50, 50), orderBook.getLowestSellOrder().get());

        orderBook.add(new Order(6, SELL, 45, 50));
        assertFalse(orderBook.getHighestBuyOrder().isPresent());
        assertEquals(new Order(6, SELL, 45, 50), orderBook.getLowestSellOrder().get());
    }

    @Test
    public void whenPriceOfBuyOrderGreaterOrEqualThanOfTheSellOrderNeedToDoMutualAnnihilation() {
        OrderBookQueue orderBook = new OrderBookQueue();

        orderBook.add(new Order(1, BUY, 50, 100));
        assertEquals(new Order(1, BUY, 50, 100), orderBook.getHighestBuyOrder().get());
        assertFalse(orderBook.getLowestSellOrder().isPresent());

        orderBook.add(new Order(2, BUY, 55, 100));
        assertEquals(new Order(2, BUY, 55, 100), orderBook.getHighestBuyOrder().get());
        assertFalse(orderBook.getLowestSellOrder().isPresent());

        orderBook.add(new Order(3, SELL, 40, 150));
        assertEquals(new Order(1, BUY, 50, 50), orderBook.getHighestBuyOrder().get());
        assertFalse(orderBook.getLowestSellOrder().isPresent());

        orderBook.add(new Order(4, SELL, 50, 50));
        assertFalse(orderBook.getHighestBuyOrder().isPresent());
        assertFalse(orderBook.getLowestSellOrder().isPresent());

        //--------------------------------------------------------------

        orderBook.add(new Order(5, SELL, 50, 50));
        assertFalse(orderBook.getHighestBuyOrder().isPresent());
        assertEquals(new Order(5, SELL, 50, 50), orderBook.getLowestSellOrder().get());

        orderBook.add(new Order(6, SELL, 45, 50));
        assertFalse(orderBook.getHighestBuyOrder().isPresent());
        assertEquals(new Order(6, SELL, 45, 50), orderBook.getLowestSellOrder().get());

        orderBook.add(new Order(7, BUY, 30, 100));
        assertEquals(new Order(7, BUY, 30, 100), orderBook.getHighestBuyOrder().get());
        assertEquals(new Order(6, SELL, 45, 50), orderBook.getLowestSellOrder().get());

        orderBook.add(new Order(8, BUY, 45, 25));
        assertEquals(new Order(7, BUY, 30, 100), orderBook.getHighestBuyOrder().get());
        assertEquals(new Order(6, SELL, 45, 25), orderBook.getLowestSellOrder().get());

        orderBook.add(new Order(8, BUY, 55, 50));
        assertEquals(new Order(7, BUY, 30, 100), orderBook.getHighestBuyOrder().get());
        assertEquals(new Order(5, SELL, 50, 25), orderBook.getLowestSellOrder().get());


    }

    @Test
    public void testCancel() {
        assertEquals(0, orderBook.size());
        assertFalse(orderBook.cancelOrder(1));

        orderBook.add(new Order(1, SELL, 50, 50));
        orderBook.add(new Order(2, SELL, 50, 50));
        orderBook.add(new Order(3, SELL, 50, 50));
        orderBook.add(new Order(4, BUY, 40, 50));
        orderBook.add(new Order(5, BUY, 40, 50));
        orderBook.add(new Order(6, BUY, 40, 50));

        assertEquals(6, orderBook.size());

        assertTrue(orderBook.cancelOrder(1));
        assertTrue(orderBook.cancelOrder(3));
        assertTrue(orderBook.cancelOrder(5));

        assertEquals(3, orderBook.size());
    }

    @Test
    public void whenThereAreSeveralOrdersWithTheSamePriceSizeWillBeSum() {
        orderBook.add(new Order(1, SELL, 50, 50));
        orderBook.add(new Order(2, SELL, 50, 50));
        orderBook.add(new Order(3, BUY, 40, 50));
        orderBook.add(new Order(4, BUY, 40, 50));

        assertEquals(Integer.valueOf(100), orderBook.getSizeByPrice(50));
        assertEquals(Integer.valueOf(100), orderBook.getSizeByPrice(40));
    }

    @Test
    public void whenThereIsNoOrdersWithDesirePriceSizeWillBeZero() {
        assertEquals(Integer.valueOf(0), orderBook.getSizeByPrice(40));
    }


}