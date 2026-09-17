package io.github.igoortoorres.paymentservice.payment.error;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
