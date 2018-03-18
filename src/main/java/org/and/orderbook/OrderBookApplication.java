package org.and.orderbook;

import org.and.orderbook.actions.Action;
import org.and.orderbook.actions.ActionsFileReader;

import java.util.stream.Stream;

public class OrderBookApplication {

    public OrderBook run(String filePath) {
        OrderBook orderBook = new OrderBook();
        ActionsFileReader parser = new ActionsFileReader(orderBook);

        try(Stream<Action> actionStream = parser.actions(filePath)) {
            actionStream.forEach(Action::execute);
        }
        return orderBook;
    }

    public static void main(String[] args) {
        if(args.length == 0) {
            throw new IllegalArgumentException("Please point action's file.");
        }
        String pathToFile = args[0];
        OrderBookApplication orderBookApp = new OrderBookApplication();
        orderBookApp.run(pathToFile);
    }


}
