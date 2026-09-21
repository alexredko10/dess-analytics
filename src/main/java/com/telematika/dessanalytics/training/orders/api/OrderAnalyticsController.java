package com.telematika.dessanalytics.training.orders.api;

import com.telematika.dessanalytics.training.orders.domain.Order;
import com.telematika.dessanalytics.training.orders.service.OrderAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class OrderAnalyticsController {

    private final OrderAnalyticsService service;

    public OrderAnalyticsController(OrderAnalyticsService service) {
        this.service = service;
    }

    @PostMapping("/expensive")
    public List<Long> expensiveOrders(
            @RequestBody List<Order> orders,
            @RequestParam int minTotalCents
    ) {
        return service.findExpensiveOrders(orders, minTotalCents);
    }

    @PostMapping("/quantity-by-sku")
    public Map<String, Integer> quantityBySku(@RequestBody List<Order> orders) {
        return service.aggregateQuantityBySku(orders);
    }

    @PostMapping("/top-skus")
    public List<String> topSkus(
            @RequestBody List<Order> orders,
            @RequestParam(defaultValue = "3") int limit
    ) {
        return service.topSkus(orders, limit);
    }
}
