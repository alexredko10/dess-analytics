package com.telematika.dessanalytics.analytics.repository;

import com.telematika.dessanalytics.analytics.domain.InverterSample;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class InMemoryTelemetryRepository implements TelemetryRepository {
    private final List<InverterSample> samples;

    public InMemoryTelemetryRepository(List<InverterSample> samples) {
        this.samples = samples;
    }

    @Override
    public List<InverterSample> findSamples(Instant from, Instant to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from must be before to");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before to");
        }
        return samples.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.timestamp() != null)
                .filter(s -> {
                    Instant timestamp = s.timestamp();
                    return !timestamp.isBefore(from) && timestamp.isBefore(to);
                })
                .sorted(Comparator.comparing(InverterSample::timestamp))
                .toList();
    }
}
