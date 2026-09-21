package io.github.igoortoorres.paymentservice.payment.domain;

import io.github.igoortoorres.paymentservice.payment.error.DomainException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Payment {

    private final UUID id;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentMethod paymentMethod;
    private final String externalReference;
    private PaymentStatus status;
    private final Instant createdAt;

    private Payment(
            UUID id,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String externalReference,
            PaymentStatus status,
            Instant createdAt
    ) {
        validateId(id);
        validateAmount(amount);
        validateCurrency(currency);
        validatePaymentMethod(paymentMethod);
        validateExternalReference(externalReference);
        validateStatus(status);
        validateCreatedAt(createdAt);

        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.externalReference = externalReference;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Payment create(
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String externalReference
    ) {
        return new Payment(
                UUID.randomUUID(),
                amount,
                currency,
                paymentMethod,
                externalReference,
                PaymentStatus.CREATED,
                Instant.now()
        );
    }

    public static Payment restore(
            UUID id,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String externalReference,
            PaymentStatus status,
            Instant createdAt
    ) {
        return new Payment(
                id,
                amount,
                currency,
                paymentMethod,
                externalReference,
                status,
                createdAt
        );
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new DomainException("Id é obrigatório");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("O valor da transação deve ser maior que zero");
        }

        if (amount.scale() > 2 || amount.precision() - amount.scale() > 17) {
            throw new DomainException("O valor deve possuir no máximo 17 dígitos inteiros e duas casas decimais");
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new DomainException("A moeda é obrigatória");
        }

        if (currency.length() != 3) {
            throw new DomainException("A moeda deve possuir exatamente 3 caracteres");
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new DomainException("O método de pagamento é obrigatório");
        }
    }

    private void validateExternalReference(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new DomainException("A referência externa é obrigatória");
        }

        if (externalReference.length() > 100) {
            throw new DomainException("A referência externa deve possuir no máximo 100 caracteres");
        }
    }

    private void validateStatus(PaymentStatus status) {
        if (status == null) {
            throw new DomainException("Status é obrigatório");
        }
    }

    private void validateCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            throw new DomainException("Data de criação é obrigatória");
        }
    }

}
