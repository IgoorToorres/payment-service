package io.github.igoortoorres.paymentservice.payment.application;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;

public record CreatePaymentResult(
        Payment payment,
        boolean created
) {
}
