package com.telematika.dessanalytics.analytics.api;

import com.telematika.dessanalytics.analytics.domain.EnergySummary;
import com.telematika.dessanalytics.analytics.service.EnergyAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    EnergyAnalyticsService energyService;

    @Test
    void shouldReturnSummary() throws Exception {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-01-02T00:00:00Z");

        when(energyService.calculateSummary(from, to))
                .thenReturn(new EnergySummary(
                        1.5,
                        2.5,
                        3.5,
                        4.5,
                        5.5)
                );

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", "2026-01-01T00:00:00Z")
                        .param("to", "2026-01-02T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pvGeneratedKwh").value(1.5))
                .andExpect(jsonPath("$.loadConsumedKwh").value(2.5))
                .andExpect(jsonPath("$.minBatterySoc").value(3.5))
                .andExpect(jsonPath("$.maxBatterySoc").value(4.5))
                .andExpect(jsonPath("$.averageBatterySoc").value(5.5));
    }
    @Test
    void shouldReturnBadRequestWhenFromIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/summary")
                        .param("from", "not-a-date")
                        .param("to", "2026-01-02T00:00:00Z"))
                .andExpect(status().isBadRequest());
    }
}