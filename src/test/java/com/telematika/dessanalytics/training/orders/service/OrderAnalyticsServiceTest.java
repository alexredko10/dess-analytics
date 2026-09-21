package com.telematika.dessanalytics.training.orders.service;


import com.telematika.dessanalytics.training.orders.domain.Order;
import com.telematika.dessanalytics.training.orders.domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderAnalyticsServiceTest {
    @MockBean
    private OrderAnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new OrderAnalyticsService();
    }

    @Test
    void findsExpensiveOrdersAndIncludesExactThreshold() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("A", 2, 500),
                        item("B", 1, 250)),
                order(2, "c2",
                        item("C", 1, 1250)),
                order(3, "c3",
                        item("D", 1, 300))
        );

        assertThat(service.findExpensiveOrders(orders, 1250))
                .containsExactly(1L, 2L);
    }

    @Test
    void sortsTopSkusBySkuAscendingWhenQuantitiesAreEqual() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("C", 5, 100),
                        item("A", 10, 100),
                        item("B", 5, 100))
        );

        assertThat(service.topSkus(orders, 3))
                .containsExactly("A", "B", "C");
    }

    @Test
    void sortsTopSkusBySkuAscendingWhenQuantitiesAreEqualTwo() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("P", 5, 100),
                        item("A", 5, 100),
                        item("B", 10, 100))
        );

        assertThat(service.topSkus(orders, 3))
                .containsExactly("B", "A", "P");
    }

    @Test
    void aggregatesQuantityForRepeatedSku() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("A", 2, 500),
                        item("B", 1, 100)),
                order(2, "c2",
                        item("A", 3, 500))
        );

        assertThat(service.aggregateQuantityBySku(orders))
                .isEqualTo(Map.of(
                        "A", 5,
                        "B", 1
                ));
    }

    @Test
    void ranksCustomersByTotalSpendDescending() {
        List<Order> orders = List.of(
                order(1, "alice", item("A", 2, 500)),
                order(2, "bob", item("B", 1, 2500)),
                order(3, "alice", item("C", 1, 2000)),
                order(4, "carol", item("D", 3, 1000))
        );

        assertThat(service.rankCustomersBySpend(orders))
                .containsExactly("alice", "carol", "bob");
    }

    @Test
    void keepsUniqueSkusInFirstSeenOrder() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("B", 1, 100),
                        item("A", 1, 100)),
                order(2, "c2",
                        item("C", 1, 100),
                        item("A", 1, 100))
        );

        assertThat(service.uniqueSkus(orders))
                .containsExactly("B", "A", "C");
    }

    @Test
    void calculatesMaximumRevenueWindow() {
        int[] revenue = {10, 20, 5, 40};

        assertThat(service.maxRevenueWindow(revenue, 2))
                .isEqualTo(45);
    }

    @Test
    void supportsWindowEqualToArrayLength() {
        int[] revenue = {10, 20, 30};

        assertThat(service.maxRevenueWindow(revenue, 3))
                .isEqualTo(60);
    }

    @Test
    void returnsTopSkusByQuantityDescending() {
        List<Order> orders = List.of(
                order(1, "c1",
                        item("A", 2, 100),
                        item("B", 5, 100)),
                order(2, "c2",
                        item("A", 4, 100),
                        item("C", 5, 100))
        );

        assertThat(service.topSkus(orders, 2))
                .containsExactly("A", "B");
    }

    private static Order order(long id, String customerId, OrderItem... items) {
        return new Order(id, customerId, List.of(items), "NEW");
    }

    private static OrderItem item(String sku, int quantity, int unitPriceCents) {
        return new OrderItem(sku, quantity, unitPriceCents);
    }

    @Test
    void findsOverlappingMaximumRevenueWindow() {
        int[] revenue = {10, 50, 50, 10};

        assertThat(service.maxRevenueWindow(revenue, 2))
                .isEqualTo(100);
    }
}
