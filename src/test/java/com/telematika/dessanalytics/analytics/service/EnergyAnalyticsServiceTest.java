package com.telematika.dessanalytics.analytics.service;

import com.telematika.dessanalytics.analytics.domain.InverterSample;
import com.telematika.dessanalytics.analytics.service.utils.EnergyCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EnergyAnalyticsServiceTest {

    private EnergyAnalyticsService energyAnalyticsService;

    @BeforeEach
    void setUp() {
        energyAnalyticsService = new EnergyAnalyticsService();
    }

    @Test
    void calculatesMinimumBatterySocIgnoringNullValues() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, 65.0),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, 52.0),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, 71.0),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, 48.0)
        );

        double result = energyAnalyticsService.calculateMinBatterySoc(samples);

        assertThat(result).isEqualTo(48.0);
    }

    @Test
    void throwsWhenNoBatterySocValuesAvailable() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, null)
        );

        assertThatThrownBy(() -> energyAnalyticsService.calculateMinBatterySoc(samples))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No battery SOC values available");
    }

    @Test
    void calculatesMaxBatterySocIgnoringNullValues() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, 65.0),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, 52.0),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, 71.0),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, 48.0)
        );

        double result = energyAnalyticsService.calculateMaxBatterySoc(samples);
        assertThat(result).isEqualTo(71.0);
    }

    @Test
    void throwsWhenNoMaxBatterySocValuesAvailable() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, null)
        );

        assertThatThrownBy(() -> energyAnalyticsService.calculateMaxBatterySoc(samples))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No battery SOC values available");
    }

    @Test
    void calculatesAvgBatterySocIgnoringNullValues() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, 65.0),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, 52.0),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, 71.0),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, 48.0)
        );

        double result = energyAnalyticsService.calculateAverageBatterySoc(samples);
        assertThat(result).isEqualTo(59.0);
    }

    @Test
    void throwsWhenNoAvgBatterySocValuesAvailable() {
        List<InverterSample> samples = List.of(
                new InverterSample(Instant.parse("2026-09-21T10:00:00Z"), 1000.0, 800.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:05:00Z"), 1100.0, 850.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:10:00Z"), 1200.0, 900.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:15:00Z"), 900.0, 750.0, null),
                new InverterSample(Instant.parse("2026-09-21T10:20:00Z"), 800.0, 700.0, null)
        );

        assertThatThrownBy(() -> energyAnalyticsService.calculateAverageBatterySoc(samples))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No battery SOC values available");
    }

    @Test
    void calculatesPvEnergyForConstantPower() {
        List<InverterSample> samples = List.of(
                new InverterSample(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        1000.0,
                        800.0,
                        70.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T10:30:00Z"),
                        1000.0,
                        800.0,
                        69.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T11:00:00Z"),
                        1000.0,
                        800.0,
                        68.0
                )
        );

        double result = energyAnalyticsService.calculatePvEnergyKwh(samples);

        assertThat(result).isEqualTo(1.0);
    }

    @Test
    void calculatesPvEnergyUsingAveragePowerBetweenSamples() {
        List<InverterSample> samples = List.of(
                new InverterSample(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        1000.0,
                        800.0,
                        70.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T10:30:00Z"),
                        1000.0,
                        800.0,
                        69.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T11:00:00Z"),
                        2000.0,
                        900.0,
                        68.0
                )
        );

        double result = energyAnalyticsService.calculatePvEnergyKwh(samples);

        assertThat(result).isEqualTo(1.25);
    }

    @Test
    void skipsIntervalsWithMissingPvPower() {
        List<InverterSample> samples = List.of(
                new InverterSample(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        1000.0, 800.0, 70.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T11:00:00Z"),
                        null, 800.0, 69.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T11:00:00Z"),
                        1000.0, 800.0, 68.0
                )
        );

        assertThat(energyAnalyticsService.calculatePvEnergyKwh(samples))
                .isEqualTo(1.0);
    }

    @Test
    void calculatesLoadConsumedEnergy() {
        List<InverterSample> samples = List.of(
                new InverterSample(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        1000.0,
                        500.0,
                        70.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T10:30:00Z"),
                        1000.0,
                        1000.0,
                        69.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T11:00:00Z"),
                        1000.0,
                        500.0,
                        68.0
                )
        );

        assertThat(energyAnalyticsService.calculateLoadConsumedKwh(samples))
                .isEqualTo(0.75);
    }

    @Test
    void calculatesLoadWhenPvGenerationIsZero() {
        List<InverterSample> samples = List.of(
                new InverterSample(
                        Instant.parse("2026-09-22T22:00:00Z"),
                        0.0,
                        1000.0,
                        70.0
                ),
                new InverterSample(
                        Instant.parse("2026-09-22T23:00:00Z"),
                        0.0,
                        1000.0,
                        60.0
                )
        );

        assertThat(energyAnalyticsService.calculateLoadConsumedKwh(samples))
                .isEqualTo(1.0);
    }

    @Test
    void shouldIntegrateOnlyInSortedOrder() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        List<InverterSample> samples = List.of(
                new InverterSample(t0.plusSeconds(1200), 1000.0, null, null),  // последний
                new InverterSample(t0, 1000.0, null, null),  // первый
                new InverterSample(t0.plusSeconds(600), 1000.0, null, null)
        );
        double kwh = EnergyCalculator.calculateEnergyKwh(
                samples, InverterSample::pvPowerW);
        assertEquals(1.0 / 3.0, kwh, 1e-9);
    }

    @Test
    void shouldSkipNullPowerValues() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        List<InverterSample> samples = List.of(
                new InverterSample(t0, 1000.0, null, null),
                new InverterSample(t0.plusSeconds(600), null, null, null),  // ← выпадет
                new InverterSample(t0.plusSeconds(1200), 1000.0, null, null)
        );

        double kwh = EnergyCalculator.calculateEnergyKwh(samples, InverterSample::pvPowerW);
        assertEquals(1.0 / 3.0, kwh, 1e-9);
    }

    @Test
    void shouldReturnZeroForInsufficientData() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");

        assertEquals(0.0, EnergyCalculator.calculateEnergyKwh(
                null, InverterSample::pvPowerW));

        assertEquals(0.0, EnergyCalculator.calculateEnergyKwh(
                List.of(), InverterSample::pvPowerW));

        assertEquals(0.0, EnergyCalculator.calculateEnergyKwh(
                List.of(new InverterSample(t0, 1000.0, null, null)),
                InverterSample::pvPowerW));
    }

    @Test
    void shouldUseTrapezoidalRule() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        List<InverterSample> samples = List.of(
                new InverterSample(t0, 0.0, null, null),
                new InverterSample(t0.plusSeconds(3600), 1000.0, null, null)
        );

        double kwh = EnergyCalculator.calculateEnergyKwh(samples, InverterSample::pvPowerW);

        assertEquals(0.5, kwh, 1e-9);
    }

    @Test
    void shouldCalculatePvAndLoadIndependently() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        List<InverterSample> samples = List.of(
                new InverterSample(t0, 500.0, 200.0, null),
                new InverterSample(t0.plusSeconds(3600), 500.0, 200.0, null)
        );

        double pv = EnergyCalculator.calculateEnergyKwh(samples, InverterSample::pvPowerW);
        double load = EnergyCalculator.calculateEnergyKwh(samples, InverterSample::totalLoadW);

        assertEquals(0.5, pv, 1e-9);
        assertEquals(0.2, load, 1e-9);
    }
}