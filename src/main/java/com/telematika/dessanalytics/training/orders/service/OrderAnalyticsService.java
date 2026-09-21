package com.telematika.dessanalytics.training.orders.service;


import com.telematika.dessanalytics.training.orders.domain.Order;
import com.telematika.dessanalytics.training.orders.domain.OrderItem;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderAnalyticsService {

    /**
     * Returns order ids whose total value is >= minTotalCents.
     * Result should preserve the input order.
     */
    public List<Long> findExpensiveOrders(List<Order> orders, int minTotalCents) {
        List<Long> result = new ArrayList<>();

        for (Order order : orders) {
            int total = 0;

            for (OrderItem item : order.items()) {
                total += item.quantity() * item.unitPriceCents();
            }

            if (total >= minTotalCents) {
                result.add(order.id());
            }
        }

        return result;
    }

    /**
     * Counts how many units of each SKU were ordered across all orders.
     */
    public Map<String, Integer> aggregateQuantityBySku(List<Order> orders) {
        Map<String, Integer> result = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.items()) {
                result.merge(item.sku(), item.quantity(), Integer::sum);
            }
        }
        return result;
    }

    /**
     * Returns customer ids ordered by total spend descending.
     * Each customer should appear once.
     * For equal spend, sort by customer id ascending.
     */
    public List<String> rankCustomersBySpend(List<Order> orders) {
        Map<String, Integer> totals = new HashMap<>();

        for (Order order : orders) {
            int orderTotal = order.items().stream()
                    .mapToInt(i -> i.unitPriceCents() * i.quantity())
                    .sum();

            totals.merge(order.customerId(), orderTotal, Integer::sum);
        }

        List<String> customers = new ArrayList<>(totals.keySet());
        customers.sort(
                Comparator.comparingInt((String c) -> totals.getOrDefault(c, 0))
                        .reversed()
                        .thenComparing(Comparator.naturalOrder())
        );

        return customers;
    }

    /**
     * Returns unique SKUs in first-seen order.
     */
    public List<String> uniqueSkus(List<Order> orders) {
        Set<String> skus = new LinkedHashSet<>();

        for (Order order : orders) {
            for (OrderItem item : order.items()) {
                skus.add(item.sku());
            }
        }

        return new ArrayList<>(skus);
    }

    /**
     * Given daily revenue values, returns the max revenue in any consecutive
     * window of 'windowSize' days.
     * <p>
     * Example: [10, 20, 5, 40], windowSize = 2 => 45
     */
    public int maxRevenueWindow(int[] dailyRevenue, int windowSize) {
        int max = 0;
        int windowSum;
        if (windowSize <= 0 || windowSize > dailyRevenue.length) {
            throw new IllegalArgumentException();
        }
        if (dailyRevenue.length <= windowSize) {
            return Arrays.stream(dailyRevenue).sum();
        }
        int to = dailyRevenue.length - 1;
        int j = windowSize - 1;

        for (int i = 0; i <= j; i++) max += dailyRevenue[i];
        windowSum = max;
        for (int i = 1; j < to; i++) {
            j++;
            windowSum = (windowSum - dailyRevenue[i - 1] + dailyRevenue[j]);
            max = Math.max(max, windowSum);
        }
        return Math.max(windowSum, max);
    }

    /**
     * Returns the top N SKUs by total quantity descending.
     * If quantities are equal, SKU ascending.
     */
    public List<String> topSkus(List<Order> orders, int limit) {
        Map<String, Integer> quantityBySku = aggregateQuantityBySku(orders);

        return quantityBySku.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry::getKey, Comparator.naturalOrder())
                )
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();

    }
}
