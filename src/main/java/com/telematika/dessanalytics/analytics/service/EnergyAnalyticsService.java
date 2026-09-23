package com.telematika.dessanalytics.analytics.service;

import com.telematika.dessanalytics.analytics.domain.EnergySummary;
import com.telematika.dessanalytics.analytics.domain.InverterSample;
import com.telematika.dessanalytics.analytics.repository.TelemetryRepository;
import com.telematika.dessanalytics.analytics.service.utils.EnergyCalculator;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class EnergyAnalyticsService {

    private static final double W_TO_KW = 1000.0;
    private final TelemetryRepository telemetryRepository;

    public EnergyAnalyticsService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    /**
     * Calculates minimum battery SOC from the given samples
     *
     * @param samples
     * @return
     */
    public double calculateMinBatterySoc(List<InverterSample> samples) {

        return samples.stream()
                .map(InverterSample::batterySoc)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("No battery SOC values available"));
    }

    /**
     * Calculates maximum battery SOC from the given samples
     *
     * @param samples
     * @return
     */
    public double calculateMaxBatterySoc(List<InverterSample> samples) {
        return samples.stream()
                .map(InverterSample::batterySoc)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("No battery SOC values available"));
    }

    /**
     * Calculates average battery SOC from the given samples
     *
     * @param samples samples
     * @return
     */
    public double calculateAverageBatterySoc(List<InverterSample> samples) {
        return samples
                .stream()
                .map(InverterSample::batterySoc)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("No battery SOC values available"));
    }

    public double calculatePvEnergyKwh(List<InverterSample> samples) {
        return EnergyCalculator.calculateEnergyKwh(samples, InverterSample::pvPowerW);
    }

    public double calculateLoadConsumedKwh(List<InverterSample> samples) {

        return EnergyCalculator.calculateEnergyKwh(samples, InverterSample::totalLoadW);
    }

    public EnergySummary calculateSummary(List<InverterSample> samples) {
        return new EnergySummary(
                calculatePvEnergyKwh(samples),
                calculateLoadConsumedKwh(samples),
                calculateMinBatterySoc(samples),
                calculateMaxBatterySoc(samples),
                calculateAverageBatterySoc(samples)
        );
    }

    public EnergySummary calculateSummary(Instant from, Instant to){
        List<InverterSample> samples = telemetryRepository.findSamples(from, to);
        return calculateSummary(samples);
    }
}
