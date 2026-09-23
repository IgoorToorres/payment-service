package io.github.igoortoorres.paymentservice.payment.error;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String message) {
        super(message);
    }
}
