package io.github.igoortoorres.paymentservice.payment.application;


import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;

import java.math.BigDecimal;

public record CreatePaymentCommand(
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String externalReference
) {
}


