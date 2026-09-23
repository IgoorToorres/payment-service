package io.github.igoortoorres.paymentservice.payment.api;

import io.github.igoortoorres.paymentservice.TestcontainersConfiguration;
import io.github.igoortoorres.paymentservice.payment.infrastructure.persistence.SpringDataPaymentRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class PaymentControllerIntegrationTests {

    private static final String IDEMPOTENCY_KEY = "payment-order-92831";

    @LocalServerPort
    private int port;

    @Autowired
    private SpringDataPaymentRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        repository.deleteAll();
    }

    @Test
    void shouldExposeOpenApiDocumentation() {
        given()
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .body("info.title", equalTo("Payment Service API"))
                .body("info.version", equalTo("1.0.0"))
                .body("paths.'/api/payments'.post.responses.'201'", notNullValue())
                .body("paths.'/api/payments'.post.responses.'200'", notNullValue())
                .body("paths.'/api/payments'.post.responses.'409'", notNullValue())
                .body("paths.'/api/payments'.post.parameters.name", hasItem("Idempotency-Key"))
                .body("paths.'/api/payments'.get.responses.'200'", notNullValue())
                .body("paths.'/api/payments/{id}'.get.responses.'404'", notNullValue());

        given()
                .when()
                .get("/swagger-ui/index.html")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldCreateFindAndListPayment() {
        String paymentId = given()
                .header("Idempotency-Key", IDEMPOTENCY_KEY)
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("amount", equalTo(199.90f))
                .body("currency", equalTo("BRL"))
                .body("paymentMethod", equalTo("PIX"))
                .body("externalReference", equalTo("ORDER-92831"))
                .body("status", equalTo("CREATED"))
                .body("createdAt", notNullValue())
                .extract()
                .path("id");

        given()
                .pathParam("id", paymentId)
                .when()
                .get("/api/payments/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(paymentId))
                .body("externalReference", equalTo("ORDER-92831"));

        given()
                .when()
                .get("/api/payments")
                .then()
                .statusCode(200)
                .body("id", hasItem(paymentId));
    }

    @Test
    void shouldRejectInvalidPayment() {
        String requestBody = """
                {
                  "amount": 0,
                  "currency": "",
                  "paymentMethod": null,
                  "externalReference": ""
                }
                """;

        given()
                .header("Idempotency-Key", "invalid-payment-key")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", equalTo("Falha na validação dos campos"))
                .body(
                        "fieldErrors.field",
                        hasItems("amount", "currency", "paymentMethod", "externalReference")
                );
    }

    @Test
    void shouldReturnExistingPaymentForRepeatedIdempotencyKey() {
        String paymentId = given()
                .header("Idempotency-Key", IDEMPOTENCY_KEY)
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .header("Idempotency-Key", IDEMPOTENCY_KEY)
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(200)
                .body("id", equalTo(paymentId));

        assertThat(repository.countByIdempotencyKey(IDEMPOTENCY_KEY)).isEqualTo(1);
    }

    @Test
    void shouldRejectIdempotencyKeyReusedWithDifferentData() {
        given()
                .header("Idempotency-Key", IDEMPOTENCY_KEY)
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(201);

        String differentRequestBody = """
                {
                  "amount": 500.00,
                  "currency": "BRL",
                  "paymentMethod": "PIX",
                  "externalReference": "ORDER-92831"
                }
                """;

        given()
                .header("Idempotency-Key", IDEMPOTENCY_KEY)
                .contentType(ContentType.JSON)
                .body(differentRequestBody)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("Conflict"))
                .body(
                        "message",
                        equalTo("A chave de idempotência já foi utilizada com dados diferentes")
                );
    }

    @Test
    void shouldRejectMissingIdempotencyKey() {
        given()
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", equalTo("O header Idempotency-Key é obrigatório"));
    }

    @Test
    void shouldRejectBlankOrOversizedIdempotencyKey() {
        given()
                .header("Idempotency-Key", " ")
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400)
                .body("message", equalTo("A chave de idempotência é obrigatória"));

        given()
                .header("Idempotency-Key", "A".repeat(101))
                .contentType(ContentType.JSON)
                .body(validRequestBody())
                .when()
                .post("/api/payments")
                .then()
                .statusCode(400)
                .body(
                        "message",
                        equalTo("A chave de idempotência deve possuir no máximo 100 caracteres")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownPayment() {
        UUID unknownId = UUID.randomUUID();

        given()
                .pathParam("id", unknownId)
                .when()
                .get("/api/payments/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not found"))
                .body("path", equalTo("/api/payments/" + unknownId));
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
