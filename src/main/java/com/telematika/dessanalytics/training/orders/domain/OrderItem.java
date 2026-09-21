package com.telematika.dessanalytics.training.orders.domain;

public record OrderItem(
        String sku,
        int quantity,
        int unitPriceCents
) {
}
