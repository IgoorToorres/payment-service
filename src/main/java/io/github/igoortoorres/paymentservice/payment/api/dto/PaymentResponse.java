package io.github.igoortoorres.paymentservice.payment.api.dto;

import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String externalReference,
        PaymentStatus status,
        Instant createdAt
) {
}

