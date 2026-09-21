package io.github.igoortoorres.paymentservice.payment.infrastructure.persistence;

import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "payments")
public class PaymentJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "external_reference", nullable = false, length = 100)
    private String externalReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PaymentJpaEntity() {
    }

    PaymentJpaEntity(
            UUID id,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String externalReference,
            PaymentStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.externalReference = externalReference;
        this.status = status;
        this.createdAt = createdAt;
    }
}
