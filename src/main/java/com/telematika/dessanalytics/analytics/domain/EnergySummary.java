package com.telematika.dessanalytics.analytics.domain;

public record EnergySummary(
        double pvGeneratedKwh,
        double loadConsumedKwh,
        double minBatterySoc,
        double maxBatterySoc,
        double averageBatterySoc
) {
}
