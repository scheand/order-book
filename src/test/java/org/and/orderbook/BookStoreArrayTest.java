package org.and.orderbook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Integer.valueOf;
import static org.and.orderbook.Order.Side.BUY;
import static org.and.orderbook.Order.Side.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BookStoreArrayTest {

    BookStoreArray bookStore;

    @Before
    public void setUp() {
        bookStore = new BookStoreArray(0);
    }

    @Test
    public void testArrayOffset() {
         BookStoreArray bookStore1000 = new BookStoreArray(1000);
         bookStore1000.add(new Order(BUY, 1000, 10));
         bookStore1000.add(new Order(BUY, 1100, 11));
         bookStore1000.add(new Order(BUY, 1111, 12));

         bookStore1000.add(new Order(SELL, 2_000, 13));
         bookStore1000.add(new Order(SELL, 5_000, 14));
         bookStore1000.add(new Order(SELL, 11_000, 15));

         Assert.assertEquals(valueOf(10), bookStore1000.sizeByPrice(1000));
         Assert.assertEquals(valueOf(11), bookStore1000.sizeByPrice(1100));
         Assert.assertEquals(valueOf(12), bookStore1000.sizeByPrice(1111));
         Assert.assertEquals(valueOf(13), bookStore1000.sizeByPrice(2000));
         Assert.assertEquals(valueOf(14), bookStore1000.sizeByPrice(5000));
         Assert.assertEquals(valueOf(15), bookStore1000.sizeByPrice(11000));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testArrayOffsetBeforeMinPrice() {
        BookStoreArray bookStore1000 = new BookStoreArray(1000);
        bookStore1000.add(new Order(BUY, 999, 10));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testArrayOffsetAfterMaxPrice() {
        BookStoreArray bookStore1000 = new BookStoreArray(1000);
        bookStore1000.add(new Order(BUY, 11_001, 10));
    }

    @Test
    public void testAddRemove() {
        bookStore.add(new Order(BUY, 40, 20));
        bookStore.add(new Order(BUY, 45, 25));
        bookStore.add(new Order(BUY, 50, 30));
        bookStore.add(new Order(BUY, 50, 30));

        bookStore.add(new Order(SELL, 60, 20));
        bookStore.add(new Order(SELL, 60, 25));
        bookStore.add(new Order(SELL, 65, 30));
        bookStore.add(new Order(SELL, 70, 40));

        Assert.assertEquals(valueOf(50), bookStore.bestBid());
        Assert.assertEquals(valueOf(60), bookStore.sizeByPrice(50));
        Assert.assertEquals(valueOf(40), bookStore.buyBoundary.getMin());
        Assert.assertEquals(valueOf(50), bookStore.buyBoundary.getMax());

        Assert.assertEquals(valueOf(60), bookStore.bestAsk());
        Assert.assertEquals(valueOf(45), bookStore.sizeByPrice(60));
        Assert.assertEquals(valueOf(60), bookStore.sellBoundary.getMin());
        Assert.assertEquals(valueOf(70), bookStore.sellBoundary.getMax());

        bookStore.removeBuy(40, 10);
        bookStore.removeBuy(40, 10);
        bookStore.removeBuy(50, 60);

        Assert.assertEquals(valueOf(45), bookStore.bestBid());
        Assert.assertEquals(valueOf(25), bookStore.sizeByPrice(45));
        Assert.assertEquals(valueOf(45), bookStore.buyBoundary.getMin());
        Assert.assertEquals(valueOf(45), bookStore.buyBoundary.getMax());

        bookStore.removeBuy(45, 25);

        Assert.assertNull(bookStore.bestBid());
        Assert.assertEquals(valueOf(0), bookStore.sizeByPrice(45));
        Assert.assertNull(bookStore.buyBoundary.getMin());
        Assert.assertNull(bookStore.buyBoundary.getMax());

        bookStore.removeSell(60, 45);
        bookStore.removeSell(70, 40);

        Assert.assertEquals(valueOf(65), bookStore.bestAsk());
        Assert.assertEquals(valueOf(30), bookStore.sizeByPrice(65));
        Assert.assertEquals(valueOf(65), bookStore.sellBoundary.getMin());
        Assert.assertEquals(valueOf(65), bookStore.sellBoundary.getMax());

        bookStore.removeSell(valueOf(65), 30);

        Assert.assertNull(bookStore.bestAsk());
        Assert.assertEquals(valueOf(0), bookStore.sizeByPrice(65));
        Assert.assertNull(bookStore.sellBoundary.getMin());
        Assert.assertNull(bookStore.sellBoundary.getMax());

        bookStore.add(new Order(BUY, 40, 20));
        bookStore.add(new Order(SELL, 60, 20));

        Assert.assertEquals(valueOf(60), bookStore.bestAsk());
        Assert.assertEquals(valueOf(40), bookStore.bestBid());

    }

    @Test
    public void testRemovingCheapestBuy() {
        bookStore.add(new Order(BUY, 40, 20));
        bookStore.add(new Order(BUY, 45, 25));
        bookStore.add(new Order(BUY, 50, 30));
        bookStore.add(new Order(BUY, 50, 30));

        bookStore.removeCheapestBuy(65);
        Assert.assertEquals(valueOf(40), bookStore.sizeByPrice(50));

        bookStore.removeCheapestBuy(20);
        Assert.assertEquals(valueOf(20), bookStore.sizeByPrice(50));

        bookStore.removeCheapestBuy(20);
        Assert.assertEquals(valueOf(0), bookStore.sizeByPrice(50));
    }


    @Test
    public void testRemovingHighestSell() {
        bookStore.add(new Order(SELL, 40, 20));
        bookStore.add(new Order(SELL, 45, 25));
        bookStore.add(new Order(SELL, 50, 30));
        bookStore.add(new Order(SELL, 50, 30));

        bookStore.removeHighestSell(60);
        Assert.assertEquals(valueOf(20), bookStore.sizeByPrice(40));
        Assert.assertEquals(valueOf(25), bookStore.sizeByPrice(45));

        bookStore.removeHighestSell(30);
        Assert.assertEquals(valueOf(15), bookStore.sizeByPrice(40));

        bookStore.removeHighestSell(30);
        Assert.assertEquals(valueOf(0), bookStore.sizeByPrice(40));
    }

    @Test
    public void testProcessing() {
        bookStore.add(new Order(1, BUY, 50, 100));
        assertEquals(valueOf(50), bookStore.bestBid());
        assertNull(bookStore.bestAsk());

        bookStore.add(new Order(2, BUY, 55, 100));
        assertEquals(valueOf(55), bookStore.bestBid());
        assertNull(bookStore.bestAsk());

        bookStore.add(new Order(3, SELL, 40, 150));
        assertEquals(valueOf(50), bookStore.bestBid());
        assertEquals(valueOf(50), bookStore.sizeByPrice(50));
        assertNull(bookStore.bestAsk());

        bookStore.add(new Order(4, SELL, 50, 50));
        assertNull(bookStore.bestBid());
        assertNull(bookStore.bestAsk());

        //--------------------------------------------------------------

        bookStore.add(new Order(5, SELL, 50, 50));
        assertNull(bookStore.bestBid());
        assertEquals(valueOf(50), bookStore.bestAsk());
        assertEquals(valueOf(50), bookStore.sizeByPrice(50));

        bookStore.add(new Order(6, SELL, 45, 50));
        assertNull(bookStore.bestBid());
        assertEquals(valueOf(45), bookStore.bestAsk());
        assertEquals(valueOf(50), bookStore.sizeByPrice(45));

        bookStore.add(new Order(7, BUY, 30, 100));
        assertEquals(valueOf(30), bookStore.bestBid());
        assertEquals(valueOf(100), bookStore.sizeByPrice(30));
        assertEquals(valueOf(45), bookStore.bestAsk());
        assertEquals(valueOf(50), bookStore.sizeByPrice(45));

        bookStore.add(new Order(8, BUY, 45, 25));
        assertEquals(valueOf(30), bookStore.bestBid());
        assertEquals(valueOf(100), bookStore.sizeByPrice(30));
        assertEquals(valueOf(45), bookStore.bestAsk());
        assertEquals(valueOf(25), bookStore.sizeByPrice(45));

        bookStore.add(new Order(9, BUY, 55, 50));
        assertEquals(valueOf(30), bookStore.bestBid());
        assertEquals(valueOf(100), bookStore.sizeByPrice(30));
        assertEquals(valueOf(50), bookStore.bestAsk());
        assertEquals(valueOf(25), bookStore.sizeByPrice(50));
    }

    @Test
    public void testCancelOrder() {
        bookStore.add(new Order(1, BUY, 50, 50));
        bookStore.add(new Order(2, BUY, 50, 70));
        bookStore.add(new Order(3, BUY, 50, 100));

        Assert.assertEquals(valueOf(220), bookStore.sizeByPrice(50));

        bookStore.cancelOrder(1);
        Assert.assertEquals(valueOf(170), bookStore.sizeByPrice(50));

        bookStore.cancelOrder(2);
        Assert.assertEquals(valueOf(100), bookStore.sizeByPrice(50));

        bookStore.cancelOrder(3);
        Assert.assertEquals(valueOf(0), bookStore.sizeByPrice(50));

    }

    @Test
    public void testBuySell() {
        bookStore.add(new Order(BUY, 40, 20));
        bookStore.add(new Order(BUY, 45, 25));
        bookStore.add(new Order(BUY, 50, 30));
        bookStore.add(new Order(BUY, 50, 30));

        bookStore.add(new Order(SELL, 60, 20));
        bookStore.add(new Order(SELL, 60, 25));
        bookStore.add(new Order(SELL, 65, 30));
        bookStore.add(new Order(SELL, 70, 40));

        bookStore.buy(55);
        Assert.assertEquals(valueOf(65), bookStore.bestAsk());
        Assert.assertEquals(valueOf(20), bookStore.sizeByPrice(65));

        bookStore.sell(65);
        Assert.assertEquals(valueOf(45), bookStore.bestBid());
        Assert.assertEquals(valueOf(20), bookStore.sizeByPrice(45));

        bookStore.buy(60);
        bookStore.sell(40);

        Assert.assertNull(bookStore.bestAsk());
        Assert.assertNull(bookStore.bestBid());

    }

    @Test
    public void testBigPrice() {
        BookStoreArray bookStore1 = new BookStoreArray(999_990_000);
        bookStore1.add(new Order(BUY, 999_990_000, 25));
        bookStore1.add(new Order(BUY, 999_990_100, 25));
        bookStore1.add(new Order(BUY, 999_990_200, 25));
        bookStore1.add(new Order(BUY, 1_000_000_000, 25));

        Assert.assertEquals(valueOf(1_000_000_000), bookStore1.bestBid());

        bookStore1.sell(70);

        Assert.assertEquals(valueOf(999_990_100), bookStore1.bestBid());
        Assert.assertEquals(valueOf(5), bookStore1.sizeByPrice(bookStore1.bestBid()));

        bookStore1.sell(5);
        Assert.assertEquals(valueOf(999_990_000), bookStore1.bestBid());

        bookStore1.sell(25);
        Assert.assertEquals(null, bookStore1.bestBid());
    }
}
