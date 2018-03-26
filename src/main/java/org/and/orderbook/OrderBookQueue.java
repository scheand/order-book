package org.and.orderbook;

import java.util.*;

import static org.and.orderbook.Order.Side.BUY;
import static org.and.orderbook.Order.Side.SELL;

//previous implementation
@Deprecated
public class OrderBookQueue {

    private final Map<Integer, Order> idOrderMap = new HashMap<>();
    private final Map<Integer, Integer> priceSizeMap = new HashMap<>();

    private final PriorityQueue<Order> buyQueue = new PriorityQueue<>(
            Comparator.comparingInt(Order::getPrice).reversed());
    private final PriorityQueue<Order> sellQueue = new PriorityQueue<>(
            Comparator.comparingInt(Order::getPrice));


    public int size() {
        return idOrderMap.size();
    }

    public void add(Order newOrder) {
        Objects.requireNonNull(newOrder, "Order must be not null.");

        Queue<Order> oppositeQueue = newOrder.getSide() == BUY ? sellQueue : buyQueue;

        Order resultOrder = newOrder;
        while (canBeMutualAnnihilated(resultOrder, oppositeQueue.peek())) {
            resultOrder = processOrders(resultOrder, pullOut(oppositeQueue.peek()));
        }

        if(resultOrder != null) {
            pushIn(resultOrder);
        }
    }

    public boolean cancelOrder(Integer id) {
        Objects.requireNonNull(id);
        Order order = idOrderMap.get(id);
        if (order == null) {
            return false;
        } else {
            pullOut(order);
            return true;
        }
    }

    public Optional<Order> getHighestBuyOrder() {
        return Optional.ofNullable(buyQueue.peek());
    }

    public Optional<Order> getLowestSellOrder() {
        return Optional.ofNullable(sellQueue.peek());
    }

    public Integer getSizeByPrice(Integer price) {
        Objects.requireNonNull(price, "Price must be nut null.");
        return priceSizeMap.containsKey(price) ? priceSizeMap.get(price) : 0;
    }

    private Order processOrders(Order newOrder, Order oppositeOrder) {
        if (newOrder.getSize() == oppositeOrder.getSize()) {
            return null;
        } else {
            int mutualAnnihilatedSize = Math.min(newOrder.getSize(), oppositeOrder.getSize());
            Order greatestBySizeOrder = newOrder.getSize() - mutualAnnihilatedSize > 0 ?
                    newOrder : oppositeOrder;
            Integer leftAfterAnnihilatingSize = greatestBySizeOrder.getSize() - mutualAnnihilatedSize;
            return new Order(greatestBySizeOrder, leftAfterAnnihilatingSize);
        }
    }

    private boolean canBeMutualAnnihilated(Order o1, Order o2) {
        if (o1 != null && o2 != null && o1.getSide() != o2.getSide()) {
            Order buyOrder = o1.getSide() == BUY ? o1 : o2;
            Order sellOrder = o1.getSide() == SELL ? o1 : o2;
            return buyOrder.getPrice() >= sellOrder.getPrice();
        } else {
            return false;
        }
    }

    private void pushIn(Order order) {
        checkOrderIdentity(order);
        idOrderMap.put(order.getId(), order);

        if (order.getSide() == BUY) {
            buyQueue.add(order);
        } else {
            sellQueue.add(order);
        }

        Integer newSize = priceSizeMap.containsKey(order.getPrice()) ?
                order.getSize() + priceSizeMap.get(order.getPrice()) :
                order.getSize();

        priceSizeMap.put(order.getPrice(), newSize);
    }

    private Order pullOut(Order order) {
        idOrderMap.remove(order.getId());

        if (order.getSide() == BUY) {
            buyQueue.remove(order);
        } else {
            sellQueue.remove(order);
        }

        Integer newSize = priceSizeMap.get(order.getPrice()) - order.getSize();
        if (newSize > 0) {
            priceSizeMap.put(order.getPrice(), newSize);
        } else {
            priceSizeMap.remove(order.getPrice());
        }

        return order;
    }

    private void checkOrderIdentity(Order order) {
        if (idOrderMap.containsKey(order.getId())) {
            throw new IllegalArgumentException("There is already order with id="
                    + order.getId() + ".");
        }
    }

}
