package com.telematika.dessanalytics.analytics.service.utils;

import com.telematika.dessanalytics.analytics.domain.InverterSample;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class EnergyCalculator {
    private static final double W_PER_KW = 1000.0;
    private static final double MS_PER_HOUR = 3_600_000.0;
    private static final double SEC_PER_HOUR = 3_600.0;


    private EnergyCalculator() {
    }

    public record EnergyTotals(
            double pvKwh,
            double loadKwh
    ) {
    }

    public static EnergyTotals calculateTotals(List<InverterSample> sorted) {
        double pvKwh = 0.0;
        double loadKwh = 0.0;
        if (sorted == null || sorted.size() < 2)
            return new EnergyTotals(pvKwh, loadKwh);
        for (int i = 0; i < sorted.size() - 1; i++) {
            InverterSample curr = sorted.get(i);
            InverterSample next = sorted.get(i + 1);
            double delta = Duration.between(curr.timestamp(), next.timestamp()).toSeconds() / SEC_PER_HOUR;
            if (curr.pvPowerW() != null && next.pvPowerW() != null) {
                pvKwh += (next.pvPowerW() + curr.pvPowerW()) / 2.0 * delta / W_PER_KW;
            }
            if (curr.totalLoadW() != null && next.totalLoadW() != null) {
                loadKwh += (next.totalLoadW() + curr.totalLoadW()) / 2.0 * delta / W_PER_KW;
            }
        }
        return new EnergyTotals(pvKwh, loadKwh);
    }

    public static double calculateEnergyKwh(List<InverterSample> samples, Function<InverterSample, Double> powerExtractor) {
        double energyKwh = 0.0;
        if (samples == null || samples.size() < 2) return energyKwh;

        List<InverterSample> sorted = samples.stream()
                .filter(s -> powerExtractor.apply(s) != null && s.timestamp() != null)
                .sorted(Comparator.comparing(InverterSample::timestamp))
                .toList();

        if (sorted.size() < 2) return energyKwh;
        
        for (int i = 0; i < sorted.size() - 1; i++) {
            InverterSample curr = sorted.get(i);
            InverterSample next = sorted.get(i + 1);
            double delta = Duration.between(curr.timestamp(), next.timestamp()).toSeconds() / SEC_PER_HOUR;
            Double currPower = powerExtractor.apply(curr);
            Double nextPower = powerExtractor.apply(next);
            if (currPower == null || nextPower == null) continue;
            energyKwh+=(currPower+nextPower)/2.0 * delta / W_PER_KW;
        }
        return energyKwh;
    }
}
