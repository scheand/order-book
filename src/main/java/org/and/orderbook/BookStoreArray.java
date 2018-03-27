package org.and.orderbook;

import java.util.Objects;

import static org.and.orderbook.Order.Side.BUY;

public class BookStoreArray implements IOrderBook {

    private static final int PRICES_LENGTH = 10_001;
    private static final int ORDERS_LENGTH = 1_000_001;
    /*index of this array is price. value is size for this price*/
    private final int[] priceArray;
    private final Order[] orders;

    private final int minPrice;

    final PriceBoundary buyBoundary = new BuyBoundaries();
    final PriceBoundary sellBoundary = new SellBoundaries();

    public BookStoreArray(int minPrice) {
        if (minPrice < 0)
            throw new IllegalArgumentException("Min Price must be greater or equal 0.");

        this.priceArray = new int[PRICES_LENGTH];
        this.orders = new Order[ORDERS_LENGTH];
        this.minPrice = minPrice;
    }

    @Override
    public void add(Order order) {
        Objects.requireNonNull(order);
        if(BUY == order.getSide()) {
            int leftSize = process(order.getPrice(), order.getSize(), sellBoundary);
            add(order.getPrice(), leftSize, buyBoundary);
        } else {
            int leftSize = process(order.getPrice(), order.getSize(), buyBoundary);
            add(order.getPrice(), leftSize, sellBoundary);
        }

        if(order.getId() != null) {
            if(orders[order.getId()] != null) {
                throw new IllegalArgumentException("Order with id " + order.getId() + " already exists in book.");
            }
            orders[order.getId()] = order;
        }
    }

    @Override
    public void buy(int size) {
        if(sellBoundary.notEmpty()) {
            process(sellBoundary.getMax(), size, sellBoundary);
        }
    }

    @Override
    public void sell(int size) {
        if(buyBoundary.notEmpty()) {
            process(buyBoundary.getMin(), size, buyBoundary);
        }
    }

    @Override
    public void cancelOrder(Integer orderId){
        Objects.requireNonNull(orderId);
        Order order = orders[orderId];
        Objects.requireNonNull(order, "Could not find order by id = " + orderId);
        if(order.getSide() == BUY) {
            removeBuy(order.getPrice(), order.getSize());
        } else {
            removeSell(order.getPrice(), order.getSize());
        }
        orders[orderId] = null;
    }

    @Override
    public Integer bestBid() {
        return buyBoundary.bestPrice();
    }

    @Override
    public Integer bestAsk() {
        return sellBoundary.bestPrice();
    }

    @Override
    public Integer sizeByPrice(int price) {
        return priceArray[indexByPrice(price)];
    }

    private void add(int price, int size, PriceBoundary boundary) {
        if (size <= 0)
            return;

        priceArray[indexByPrice(price)] = priceArray[indexByPrice(price)] + size;
        if (boundary.getMax() == null && boundary.getMin() == null) {
            boundary.setMin(price);
            boundary.setMax(price);
        } else {
            boundary.setMin(Math.min(price, boundary.getMin()));
            boundary.setMax(Math.max(price, boundary.getMax()));
        }
    }


    void removeBuy(int price, int size) {
        removeByPrice(price, size, buyBoundary);
    }

    void removeSell(int price, int size) {
        removeByPrice(price, size, sellBoundary);
    }

    void removeCheapestBuy(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater then 0.");
        }
        int leftSize = size;
        Integer price;
        while (leftSize > 0 && (price = buyBoundary.getMin()) != null) {
            int sizeForRemove = Math.min(leftSize, sizeByPrice(price));
            removeBuy(price, sizeForRemove);
            leftSize = leftSize - sizeForRemove;
        }
    }

    void removeHighestSell(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater then 0.");
        }
        int leftSize = size;
        Integer price;
        while (leftSize > 0 && (price = sellBoundary.getMax()) != null) {
            int sizeForRemove = Math.min(leftSize, sizeByPrice(price));
            removeSell(price, sizeForRemove);
            leftSize = leftSize - sizeForRemove;
        }

    }

    private void removeByPrice(int price, int size, PriceBoundary boundary) {
        if (boundary.notEmpty()) {
            if (price > boundary.getMax() || price < boundary.getMin()) {
                throw new IllegalArgumentException("Illegal price " + price +
                        " for remove. Must be between " + boundary.getMin() + " and " + boundary.getMax() + ".");
            }
            int leftSize = sizeByPrice(price) - size;
            priceArray[indexByPrice(price)] = leftSize;

            if (leftSize == 0) {
                int p;
                if (boundary.getMin().equals(boundary.getMax())) {
                    boundary.setMin(null);
                    boundary.setMax(null);
                } else if (boundary.getMin() == price) {
                    for (p = price; p <= boundary.getMax() && sizeByPrice(p) == 0; p++) ;
                    boundary.setMin(p);
                } else if (boundary.getMax() == price) {
                    for (p = price; p >= boundary.getMin() && sizeByPrice(p) == 0; p--) ;
                    boundary.setMax(p);
                }
            }
        }
    }

    private int process(int price, int size, PriceBoundary oppositeBoundary) {
        int leftSize = size;
        while (oppositeBoundary.canBeMutualAnnihilated(price) && leftSize > 0) {
            int bestOppositeSize = sizeByPrice(oppositeBoundary.bestPrice());
            int sizeToRemove = Math.min(leftSize, bestOppositeSize);
            removeByPrice(oppositeBoundary.bestPrice(), sizeToRemove, oppositeBoundary);
            leftSize = leftSize - sizeToRemove;
        }
        return leftSize;

    }

    private int indexByPrice(int price) {
        return price - minPrice;
    }

    static class SellBoundaries extends PriceBoundary {

        @Override
        public Integer bestPrice() {
            return getMin();
        }

        @Override
        public boolean canBeMutualAnnihilated(int oppositePrice) {
            return notEmpty() && oppositePrice >= bestPrice();
        }
    }

    static class BuyBoundaries extends PriceBoundary {

        @Override
        public Integer bestPrice() {
            return getMax();
        }

        @Override
        public boolean canBeMutualAnnihilated(int oppositePrice) {
            return notEmpty() && oppositePrice <= bestPrice();
        }
    }

    static abstract class PriceBoundary {
        private Integer min;
        private Integer max;

        public Integer getMax() {
            return max;
        }

        public Integer getMin() {
            return min;
        }

        public void setMax(Integer max) {
            this.max = max;
        }

        public void setMin(Integer min) {
            this.min = min;
        }

        public abstract Integer bestPrice();

        public abstract boolean canBeMutualAnnihilated(int oppositePrice);

        public boolean notEmpty() {
            return min != null && max != null;
        }
    }

}
