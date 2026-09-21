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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
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
    void shouldExposeOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Payment Service API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths['/api/payments'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/payments'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/payments/{id}'].get.responses['404']").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

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

        MvcResult creationResult = mockMvc.perform(post("/api/payments")
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

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.externalReference").value("ORDER-92831"));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(paymentId)));
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

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Falha na validação dos campos"))
                .andExpect(jsonPath(
                        "$.fieldErrors[*].field",
                        hasItems("amount", "currency", "paymentMethod", "externalReference")
                ));
    }

    @Test
    void shouldReturnNotFoundForUnknownPayment() throws Exception {
        UUID unknownId = UUID.randomUUID();

        mockMvc.perform(get("/api/payments/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.path").value("/api/payments/" + unknownId));
    }
}
