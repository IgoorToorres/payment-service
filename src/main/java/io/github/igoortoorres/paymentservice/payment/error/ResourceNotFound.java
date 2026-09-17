package io.github.igoortoorres.paymentservice.payment.error;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {
        super(message);
    }
}
