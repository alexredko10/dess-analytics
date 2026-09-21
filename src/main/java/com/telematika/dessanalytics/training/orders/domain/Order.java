package com.telematika.dessanalytics.training.orders.domain;

import java.util.List;

public record Order(
        long id,
        String customerId,
        List<OrderItem> items,
        String status
) {
}
