package com.telematika.dessanalytics.training.orders.api;

import com.telematika.dessanalytics.training.orders.service.OrderAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderAnalyticsController.class)
class OrderAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderAnalyticsService service;

    @Test
    void exposesExpensiveOrdersEndpoint() throws Exception {
        when(service.findExpensiveOrders(anyList(), eq(1000)))
                .thenReturn(List.of(10L, 20L));

        mockMvc.perform(post("/api/analytics/expensive")
                        .queryParam("minTotalCents", "1000")
                        .contentType("application/json")
                        .content("""
                                [
                                  {
                                    "id": 10,
                                    "customerId": "c1",
                                    "items": [
                                      {"sku": "A", "quantity": 2, "unitPriceCents": 500}
                                    ],
                                    "status": "NEW"
                                  }
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("[10,20]"));
    }
}
