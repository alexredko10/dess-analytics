package com.telematika.dessanalytics.analytics.api;

import com.telematika.dessanalytics.analytics.domain.EnergySummary;
import com.telematika.dessanalytics.analytics.service.EnergyAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final EnergyAnalyticsService energyService;

    public AnalyticsController(EnergyAnalyticsService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/summary")
    public ResponseEntity <EnergySummary> getSummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to
    ) {
        EnergySummary energySummary = energyService.calculateSummary(from, to);
        return ResponseEntity.ok(energySummary);
    }
}
