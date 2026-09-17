package io.github.igoortoorres.paymentservice.payment.infrastructure;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();

    @Override
    public Payment save(Payment payment) {
        payments.add(payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return payments.stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return List.copyOf(payments);
    }
}
