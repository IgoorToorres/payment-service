package io.github.igoortoorres.paymentservice.payment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    List<Payment> findAll();
}
