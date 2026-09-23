package com.telematika.dessanalytics.analytics.repository;

import com.telematika.dessanalytics.analytics.domain.InverterSample;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TelemetryRepository {

    /**
     * Returns samples in chronological order for:
     * fromInclusive <= timestamp < toExclusive
     */
    List<InverterSample> findSamples(
            Instant fromInclusive,
            Instant toExclusive
    );
}
