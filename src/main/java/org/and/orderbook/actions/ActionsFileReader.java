package org.and.orderbook.actions;

import org.and.orderbook.Order;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ActionsFileReader {

    private int minPrice;
    private List<Action> actions;

    public static ActionsFileReader firstTaskReader() {
        return new FirstTaskParser();
    }

    public static ActionsFileReader secondTaskReader() {
        return new SecondTaskParser();
    }

    public int getMinPrice() {
        return minPrice;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void parse(String pathToFile) {
        try {
            this.minPrice = 0;
            Path path = Paths.get(pathToFile);
            this.actions = Files.lines(path).map(this::parseAction)
                    .filter(o -> o.isPresent())
                    .map(o -> o.get())
                    .peek(this::minPriceCalc)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Could not parse actions file '" + pathToFile + "'. " + e.getMessage());
            throw new CouldNotParseActionsFile(e);
        }
    }

    private void minPriceCalc(Action action) {
        if (action instanceof NewOrderAction) {
            Order order = ((NewOrderAction) action).getOrder();
            minPrice = Math.min(minPrice, order.getPrice());
        }
    }

    Optional<Action> parseAction(String line) {
        String[] columns = line.split(",");

        try {
            return Optional.of(parseLine(columns));
        } catch (Exception ex) {
            System.out.println("Could not parse '" + line + "'." + ex.getMessage());
            return Optional.empty();
        }
    }

    protected abstract Action parseLine(String[] columns);

    private static class SecondTaskParser extends ActionsFileReader {

        @Override
        protected Action parseLine(String[] columns) {
            String actionCode = columns[0];
            switch (actionCode) {
                case "u":
                    return parseNewOrder(columns);
                case "o":
                    return parseRemove(columns);
                case "q":
                    return parseQuery(columns);

                default:
                    throw new IllegalArgumentException("Unknown action code '" + actionCode + "'.");
            }
        }

        private Action parseNewOrder(String[] column) {
            int price = Integer.valueOf(column[1]);
            int size = Integer.valueOf(column[2]);
            Order.Side side = parseSide(column[3]);
            return new NewOrderAction(new Order(side, price, size));
        }

        private Order.Side parseSide(String sideStr) {
            if ("bid".equalsIgnoreCase(sideStr)) {
                return Order.Side.BUY;
            } else if ("ask".equalsIgnoreCase(sideStr)) {
                return Order.Side.SELL;
            } else {
                throw new IllegalArgumentException("Could parse side from '" + sideStr + "'.");
            }
        }

        private Action parseRemove(String[] columns) {
            String removeType = columns[1];
            Integer size = Integer.valueOf(columns[2]);
            if("buy".equalsIgnoreCase(removeType)) {
                return new BuyAction(size);
            } else if("sell".equalsIgnoreCase(removeType)) {
                return new SellAction(size);
            } else {
                throw new IllegalArgumentException("Unknown remove type " + removeType);
            }
        }

        private Action parseQuery(String[] column) {
            String queryType = column[1];
            if ("best_bid".equalsIgnoreCase(queryType)) {
                return new BestBidAction();
            } else if ("best_ask".equalsIgnoreCase(queryType)) {
                return new BestAskAction();
            } else if ("size".equalsIgnoreCase(queryType)) {
                Integer price = Integer.valueOf(column[2]);
                return new FindSizeByPriceAction(price);
            } else {
                throw new IllegalArgumentException("Unknown query '" + queryType + "'.");
            }
        }

    }

    private static class FirstTaskParser extends ActionsFileReader {

        @Override
        protected Action parseLine(String[] columns) {
            String actionCode = columns[0];
            switch (actionCode) {
                case "o":
                    return parseNewOrderLine(columns);
                case "c":
                    return parseCancel(columns);
                case "q":
                    return parseQuery(columns);

                default:
                    throw new IllegalArgumentException("Unknown action code '" + actionCode + "'.");
            }
        }


        private Action parseNewOrderLine(String[] column) {
            int id = Integer.valueOf(column[1]);
            Order.Side side = parseSide(column[2]);
            int price = Integer.valueOf(column[3]);
            int size = Integer.valueOf(column[4]);
            return new NewOrderAction(new Order(id, side, price, size));
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
            return new CancelOrderAction(id);
        }

        private Action parseQuery(String[] column) {
            String queryType = column[1];
            if ("buyers".equalsIgnoreCase(queryType)) {
                return new BestBidAction();
            } else if ("sellers".equalsIgnoreCase(queryType)) {
                return new BestAskAction();
            } else if ("size".equalsIgnoreCase(queryType)) {
                Integer price = Integer.valueOf(column[2]);
                return new FindSizeByPriceAction(price);
            } else {
                throw new IllegalArgumentException("Unknown query '" + queryType + "'.");
            }
        }

    }

}
