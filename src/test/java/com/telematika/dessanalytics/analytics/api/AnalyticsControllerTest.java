package com.telematika.dessanalytics.analytics.api;

import com.telematika.dessanalytics.analytics.api.errors.NoTelemetryDataException;
import com.telematika.dessanalytics.analytics.api.rest.AnalyticsController;
import com.telematika.dessanalytics.analytics.domain.EnergySummary;
import com.telematika.dessanalytics.analytics.service.EnergyAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    private static final Instant FROM = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant TO = Instant.parse("2026-01-02T00:00:00Z");

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EnergyAnalyticsService energyService;
    @Autowired
    private EnergyAnalyticsService energyAnalyticsService;

    @Test
    void shouldReturnSummary() throws Exception {
        when(energyService.calculateSummary(FROM, TO))
                .thenReturn(new EnergySummary(1.5, 2.5, 3.5, 4.5, 5.5));

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", FROM.toString())
                        .param("to", TO.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pvGeneratedKwh").value(1.5))
                .andExpect(jsonPath("$.loadConsumedKwh").value(2.5))
                .andExpect(jsonPath("$.minBatterySoc").value(3.5))
                .andExpect(jsonPath("$.maxBatterySoc").value(4.5))
                .andExpect(jsonPath("$.averageBatterySoc").value(5.5));

        verify(energyService).calculateSummary(FROM, TO);
    }

    @Test
    void shouldReturnBadRequestWhenFromIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", "not-a-date")
                        .param("to",   TO.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Parse attempt failed for value [not-a-date]"))
                .andExpect(jsonPath("$.path").value("/api/v1/analytics/summary"));

        verifyNoInteractions(energyService);
    }

    @Test
    void shouldReturnBadRequestWhenFromIsAfterTo() throws Exception {
        Instant from = Instant.parse("2026-01-02T00:00:00Z");
        Instant to   = Instant.parse("2026-01-01T00:00:00Z");

        when(energyService.calculateSummary(from, to))
                .thenThrow(new IllegalArgumentException("from must be before to"));

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", from.toString())
                        .param("to",   to.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("from must be before to"))
                .andExpect(jsonPath("$.path").value("/api/v1/analytics/summary"));

        verify(energyService).calculateSummary(from, to);
    }
    @Test
    void shouldReturn404WhenNoData() throws Exception {
        when(energyService.calculateSummary(FROM, TO))
                .thenThrow(new NoTelemetryDataException("No telemetry data available for requested period"));

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", FROM.toString())
                        .param("to",   TO.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturn404WhenNoTelemetryData() throws Exception {
        when(energyService.calculateSummary(FROM, TO))
                .thenThrow(new NoTelemetryDataException("No telemetry data available for requested period"));

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", FROM.toString())
                        .param("to",   TO.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No telemetry data available for requested period"));
    }
}