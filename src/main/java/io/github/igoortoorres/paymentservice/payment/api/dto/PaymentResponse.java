package io.github.igoortoorres.paymentservice.payment.api.dto;

import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Pagamento criado ou consultado")
public record PaymentResponse(
        @Schema(example = "f2a4ac04-d1cc-4b3d-97f1-619cd72435e8") UUID id,
        @Schema(example = "199.90") BigDecimal amount,
        @Schema(example = "BRL") String currency,
        @Schema(example = "PIX") PaymentMethod paymentMethod,
        @Schema(example = "ORDER-92831") String externalReference,
        @Schema(example = "CREATED") PaymentStatus status,
        @Schema(example = "2026-09-17T12:00:00Z") Instant createdAt
) {
}
