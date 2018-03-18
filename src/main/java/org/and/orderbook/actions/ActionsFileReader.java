package org.and.orderbook.actions;

import org.and.orderbook.Order;
import org.and.orderbook.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ActionsFileReader {

    private static final Logger LOG = LoggerFactory.getLogger(ActionsFileReader.class);

    final OrderBook orderBook;

    public ActionsFileReader(OrderBook orderBook) {
        Objects.requireNonNull(orderBook);
        this.orderBook = orderBook;
    }

    public Stream<Action> actions(String pathToFile) throws CouldNotParseActionsFile {
        try {
            Path path = Paths.get(pathToFile);
            return Files.lines(path).map(this::parseLine)
                    .filter(o -> o.isPresent())
                    .map(o -> o.get());

        } catch (Exception e) {
            LOG.error("Could not parse actions file '"+pathToFile+"'.", e);
            throw new CouldNotParseActionsFile(e);
        }
    }


    Optional<Action> parseLine(String line) {
        String[] columns = line.split(",");

        String actionCode = columns[0];

        try {
            switch (actionCode) {
                case "o":
                    return Optional.of(parseNewOrderLine(columns));
                case "c":
                    return Optional.of(parseCancel(columns));
                case "q":
                    return Optional.of(parseQuery(columns));

                default:
                    throw new IllegalArgumentException("Unknown action code '" + actionCode + "'.");
            }
        } catch (Exception ex) {
            LOG.error("Could not parse '" + line + "'.", ex);
            return Optional.empty();
        }
    }

    private Action parseNewOrderLine(String[] column) {
        int id = Integer.valueOf(column[1]);
        Order.Side side = parseSide(column[2]);
        int price = Integer.valueOf(column[3]);
        int size = Integer.valueOf(column[4]);
        return new NewOrderAction(orderBook, new Order(id, side, price, size));
    }

    private Order.Side parseSide(String sideStr) {
        if ("b".equalsIgnoreCase(sideStr)) {
            return Order.Side.BUY;
        } else if ("s".equalsIgnoreCase(sideStr)) {
            return Order.Side.SELL;
        } else {
            throw new IllegalArgumentException("Could parse side from '" + sideStr + "'.");
        }
    }

    private Action parseCancel(String[] column) {
        int id = Integer.valueOf(column[1]);
        return new CancelOrderAction(orderBook, id);
    }

    private Action parseQuery(String[] column) {
        String queryType = column[1];
        if ("buyers".equalsIgnoreCase(queryType)) {
            return new HighestPriceOfBuyAction(orderBook);
        } else if ("sellers".equalsIgnoreCase(queryType)) {
            return new LowestPriceOfSellAction(orderBook);
        } else if ("size".equalsIgnoreCase(queryType)) {
            Integer price = Integer.valueOf(column[2]);
            return new FindSizeByPriceAction(orderBook, price);
        } else {
            throw new IllegalArgumentException("Unknown query '" + queryType + "'.");
        }
    }


}
