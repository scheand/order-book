package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.and.orderbook.OrderBook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.and.orderbook.Order.Side.BUY;
import static org.junit.Assert.assertEquals;


public class ActionsFileReaderTest {


    ActionsFileReader parser;
    OrderBook orderBookMock;

    @Before
    public void setUp() {
        orderBookMock = Mockito.mock(OrderBook.class);
        parser = new ActionsFileReader(orderBookMock);
    }

    @Test
    public void testParseActions() {
        assertEquals(
                Optional.of(new NewOrderAction(orderBookMock, new Order(1, BUY, 55, 100))),
                parser.parseLine("o,1,B,55,100"));

        assertEquals(Optional.of(new CancelOrderAction(orderBookMock, 1)),
                parser.parseLine("c,1"));

        assertEquals(Optional.of(new FindSizeByPriceAction(orderBookMock, 50)),
                parser.parseLine("q,size,50"));

        assertEquals(Optional.of(new HighestPriceOfBuyAction(orderBookMock)),
                parser.parseLine("q,buyers"));

        assertEquals(Optional.of(new LowestPriceOfSellAction(orderBookMock)),
                parser.parseLine("q,sellers"));
    }

    @Test
    public void whenCouldNotParseActionReturnEmptyOptionalAndLogException() {
        assertEquals(
                Optional.empty(),
                parser.parseLine("o,1,B,55s,100s"));

        assertEquals(
                Optional.empty(),
                parser.parseLine("o,1,SIDE,55s,100s"));

        assertEquals(Optional.empty(),
                parser.parseLine("cmd,2,2,2,2"));

    }



}