package io.github.igoortoorres.paymentservice.payment.application;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import io.github.igoortoorres.paymentservice.payment.error.IdempotencyConflictException;
import io.github.igoortoorres.paymentservice.payment.error.ResourceNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CreatePaymentResult create(CreatePaymentCommand command) {
        Payment requestedPayment = Payment.create(
                command.amount(),
                command.currency(),
                command.paymentMethod(),
                command.externalReference(),
                command.idempotencyKey()
        );

        return repository.findByIdempotencyKey(command.idempotencyKey())
                .map(existing -> resultForExisting(existing, requestedPayment))
                .orElseGet(() -> createAtomically(requestedPayment));
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

    private boolean hasSameCreationData(
            Payment existing,
            Payment requested
    ) {
        return existing.getAmount().compareTo(requested.getAmount()) == 0
                && Objects.equals(existing.getCurrency(), requested.getCurrency())
                && existing.getPaymentMethod() == requested.getPaymentMethod()
                && Objects.equals(
                        existing.getExternalReference(),
                        requested.getExternalReference()
                );
    }

    private CreatePaymentResult createAtomically(Payment requestedPayment) {
        if (repository.saveIfIdempotencyKeyAbsent(requestedPayment)) {
            return new CreatePaymentResult(requestedPayment, true);
        }

        Payment concurrentPayment = repository.findByIdempotencyKey(requestedPayment.getIdempotencyKey())
                .orElseThrow(() -> new IllegalStateException(
                        "Não foi possível recuperar o pagamento criado concorrentemente"
                ));

        return resultForExisting(concurrentPayment, requestedPayment);
    }

    private CreatePaymentResult resultForExisting(Payment existing, Payment requested) {
        if (!hasSameCreationData(existing, requested)) {
            throw new IdempotencyConflictException(
                    "A chave de idempotência já foi utilizada com dados diferentes"
            );
        }

        return new CreatePaymentResult(existing, false);
    }
}
