package io.github.igoortoorres.paymentservice.payment.application;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import io.github.igoortoorres.paymentservice.payment.error.ResourceNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Payment create(CreatePaymentCommand command) {
        Payment payment = Payment.create(
                command.amount(),
                command.currency(),
                command.paymentMethod(),
                command.externalReference()
        );

        return repository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Pagamento não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return repository.findAll();
    }
}
