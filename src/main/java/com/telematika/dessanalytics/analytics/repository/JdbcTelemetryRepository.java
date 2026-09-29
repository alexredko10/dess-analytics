package com.telematika.dessanalytics.analytics.repository;

import com.telematika.dessanalytics.analytics.domain.InverterSample;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.List;

public class JdbcTelemetryRepository implements TelemetryRepository  {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTelemetryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<InverterSample> findSamples(Instant fromInclusive, Instant toExclusive) {
        return List.of();
    }
}
