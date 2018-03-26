package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.junit.Test;

import java.util.Optional;

import static org.and.orderbook.Order.Side.BUY;
import static org.junit.Assert.assertEquals;


public class ActionsFileReaderTest {


    @Test
    public void testParseActions() {
        ActionsFileReader parser = ActionsFileReader.firstTaskReader();
        assertEquals(
                Optional.of(new NewOrderAction(new Order(1, BUY, 55, 100))),
                parser.parseAction("o,1,B,55,100"));

        assertEquals(Optional.of(new CancelOrderAction(1)),
                parser.parseAction("c,1"));

        assertEquals(Optional.of(new FindSizeByPriceAction(50)),
                parser.parseAction("q,size,50"));

        assertEquals(Optional.of(new BestBidAction()),
                parser.parseAction("q,buyers"));

        assertEquals(Optional.of(new BestAskAction()),
                parser.parseAction("q,sellers"));
    }

    @Test
    public void whenCouldNotParseActionReturnEmptyOptionalAndLogException() {
        ActionsFileReader parser = ActionsFileReader.firstTaskReader();
        assertEquals(
                Optional.empty(),
                parser.parseAction("o,1,B,55s,100s"));

        assertEquals(
                Optional.empty(),
                parser.parseAction("o,1,SIDE,55s,100s"));

        assertEquals(Optional.empty(),
                parser.parseAction("cmd,2,2,2,2"));

    }


}