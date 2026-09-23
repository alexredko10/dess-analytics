package com.telematika.dessanalytics.analytics.domain;

import java.time.Instant;

public record InverterSample(
        Instant timestamp,
        Double pvPowerW,
        Double totalLoadW,
        Double batterySoc) {
}
