package io.github.igoortoorres.paymentservice.payment.error;

public record FieldErrorResponse(
        String field,
        String message
) {
}
