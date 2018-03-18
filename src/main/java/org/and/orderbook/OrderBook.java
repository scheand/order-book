package org.and.orderbook;

import java.util.*;

import static org.and.orderbook.Order.Side.BUY;
import static org.and.orderbook.Order.Side.SELL;

public class OrderBook {

    private final Map<Integer, Order> idOrderMap = new HashMap<>();
    private final PriceSizeStore priceSizeStore = new PriceSizeStore();

    private final PriorityQueue<Order> buyQueue = new PriorityQueue<>(
            Comparator.comparingInt(Order::getPrice).reversed());
    private final PriorityQueue<Order> sellQueue = new PriorityQueue<>(
            Comparator.comparingInt(Order::getPrice));

    public int size() {
        return idOrderMap.size();
    }

    public void add(Order order) {
        Objects.requireNonNull(order, "Order must b not null.");
        if (idOrderMap.containsKey(order.getId())) {
            throw new IllegalArgumentException("There is already order with id=" + order.getId() + ".");
        }

        if (BUY == order.getSide()) {
            process(sellQueue, order);
        } else if (SELL == order.getSide()) {
            process(buyQueue, order);
        } else {
            throw new IllegalArgumentException("Unknown side value '" + order.getSide() + "'");
        }
    }

    public boolean cancelOrder(Integer id) {
        Objects.requireNonNull(id);
        Order order = idOrderMap.get(id);
        if (order == null) {
            return false;
        } else {
            pull(order);
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
        return priceSizeStore.getSize(price);
    }

    private void process(PriorityQueue<Order> oppositeQueue, Order order) {

        Order leftOrderToTarget = order;

        while (canProcessOrders(oppositeQueue.peek(), leftOrderToTarget)) {
            Order bestOppositeOrder = pull(oppositeQueue.peek());

            int minSize = Math.min(bestOppositeOrder.getSize(), leftOrderToTarget.getSize());
            int leftSizeToOpposite = bestOppositeOrder.getSize() - minSize;
            int leftSizeToTarget = leftOrderToTarget.getSize() - minSize;

            if (leftSizeToOpposite > 0) {
                push(new Order(bestOppositeOrder, leftSizeToOpposite));
            }

            if (leftSizeToTarget > 0) {
                leftOrderToTarget = new Order(leftOrderToTarget, leftSizeToTarget);
            } else {
                leftOrderToTarget = null;
            }

        }

        if (leftOrderToTarget != null) {
            push(leftOrderToTarget);
        }

    }

    private boolean canProcessOrders(Order o1, Order o2) {
        if (o1 != null && o2 != null) {
            Order buyOrder = o1.getSide() == BUY ? o1 : o2;
            Order sellOrder = o1.getSide() == SELL ? o1 : o2;
            return buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0;
        } else {
            return false;
        }
    }

    private void push(Order order) {
        if (order.getSide() == BUY) {
            buyQueue.add(order);
        } else {
            sellQueue.add(order);
        }
        priceSizeStore.add(order);
        idOrderMap.put(order.getId(), order);
    }

    private Order pull(Order order) {
        if (order.getSide() == BUY) {
            buyQueue.remove(order);
        } else {
            sellQueue.remove(order);
        }
        priceSizeStore.remove(order);
        idOrderMap.remove(order.getId());
        return order;
    }


    private static class PriceSizeStore {

        Map<Integer, List<Order>> priceOrdersMap = new HashMap<>();

        public void add(Order order) {
            if (!priceOrdersMap.containsKey(order.getPrice())) {
                priceOrdersMap.put(order.getPrice(), new ArrayList<>());
            }
            priceOrdersMap.get(order.getPrice()).add(order);

        }

        public void remove(Order order) {
            List<Order> orders = priceOrdersMap.get(order.getPrice());
            if (order == null) {
                throw new IllegalStateException("Could not find orders by price=" + order.getPrice());
            }
            orders.remove(order);
            if (orders.isEmpty()) {
                priceOrdersMap.remove(order.getPrice());
            }
        }

        public Integer getSize(Integer price) {
            List<Order> orders = priceOrdersMap.get(price);
            if(orders == null || orders.isEmpty()) {
                return 0;
            } else {
                return orders.stream().mapToInt(Order::getSize).sum();
            }
        }


    }


}
