package io.github.igoortoorres.paymentservice.payment.api;

import com.jayway.jsonpath.JsonPath;
import io.github.igoortoorres.paymentservice.payment.infrastructure.persistence.SpringDataPaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerIntegrationTests {

    private static final String IDEMPOTENCY_KEY = "payment-order-92831";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataPaymentRepository repository;

    @Test
    void shouldExposeOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Payment Service API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths['/api/payments'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/payments'].post.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/payments'].post.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/api/payments'].post.parameters[0].name")
                        .value("Idempotency-Key"))
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
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
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
                        .header("Idempotency-Key", "invalid-payment-key")
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
    void shouldReturnExistingPaymentForRepeatedIdempotencyKey() throws Exception {
        String requestBody = validRequestBody();

        MvcResult firstRequest = mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String paymentId = JsonPath.read(
                firstRequest.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId));

        assertThat(repository.countByIdempotencyKey(IDEMPOTENCY_KEY)).isEqualTo(1);
    }

    @Test
    void shouldRejectIdempotencyKeyReusedWithDifferentData() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isCreated());

        String differentRequestBody = """
                {
                  "amount": 500.00,
                  "currency": "BRL",
                  "paymentMethod": "PIX",
                  "externalReference": "ORDER-92831"
                }
                """;

        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(differentRequestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(
                        "A chave de idempotência já foi utilizada com dados diferentes"
                ));
    }

    @Test
    void shouldRejectMissingIdempotencyKey() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "O header Idempotency-Key é obrigatório"
                ));
    }

    @Test
    void shouldRejectBlankOrOversizedIdempotencyKey() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", " ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A chave de idempotência é obrigatória"
                ));

        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", "A".repeat(101))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A chave de idempotência deve possuir no máximo 100 caracteres"
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

    private String validRequestBody() {
        return """
                {
                  "amount": 199.90,
                  "currency": "BRL",
                  "paymentMethod": "PIX",
                  "externalReference": "ORDER-92831"
                }
                """;
    }
}
