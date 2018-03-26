package org.and.orderbook;

import org.and.orderbook.actions.ActionsFileReader;

public class OrderBookApplication {

    public IOrderBook runFirstTask(String filePath) {
        ActionsFileReader reader = ActionsFileReader.firstTaskReader();
        reader.parse(filePath);
        return run(reader);
    }

    public IOrderBook runSecondTask(String filePath) {
        ActionsFileReader reader = ActionsFileReader.secondTaskReader();
        reader.parse(filePath);
        return run(reader);
    }

    private IOrderBook run(ActionsFileReader reader) {
        IOrderBook orderBook = new BookStoreArray(reader.getMinPrice());
        reader.getActions().forEach(action -> action.apply(orderBook));
        return orderBook;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please point action's file.");
        }
        String pathToFile = args[0];
        OrderBookApplication orderBookApp = new OrderBookApplication();
        orderBookApp.runFirstTask(pathToFile);
    }


}
