package io.github.igoortoorres.paymentservice.payment.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateFindAndListPayment() throws Exception {
        String requestBody = """
                {
                  "amount": 199.90,
                  "currency": "BRL",
                  "paymentMethod": "PIX",
                  "externalReference": "ORDER-92831"
                }
                """;

        MvcResult creationResult = mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(199.90))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.paymentMethod").value("PIX"))
                .andExpect(jsonPath("$.externalReference").value("ORDER-92831"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn();

        String paymentId = JsonPath.read(
                creationResult.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc.perform(get("/api/payment/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.externalReference").value("ORDER-92831"));

        mockMvc.perform(get("/api/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(paymentId));
    }

    @Test
    void shouldRejectInvalidPayment() throws Exception {
        String requestBody = """
                {
                  "amount": 0,
                  "currency": "",
                  "paymentMethod": null,
                  "externalReference": ""
                }
                """;

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForUnknownPayment() throws Exception {
        UUID unknownId = UUID.randomUUID();

        mockMvc.perform(get("/api/payment/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.path").value("/api/payment/" + unknownId));
    }
}
