package io.github.igoortoorres.paymentservice.payment.domain;

import io.github.igoortoorres.paymentservice.payment.error.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTests {

    @Test
    void shouldCreatePaymentWithInitialState() {
        Payment payment = Payment.create(
                new BigDecimal("199.90"),
                "BRL",
                PaymentMethod.PIX,
                "ORDER-92831"
        );

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(payment.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRestoreEveryPersistedField() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-09-21T12:00:00Z");

        Payment payment = Payment.restore(
                id,
                new BigDecimal("99.99"),
                "BRL",
                PaymentMethod.CREDIT_CARD,
                "ORDER-1",
                PaymentStatus.CREATED,
                createdAt
        );

        assertThat(payment.getId()).isEqualTo(id);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(payment.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldRejectValuesThatDoNotFitPersistenceSchema() {
        assertThatThrownBy(() -> Payment.create(
                new BigDecimal("1.999"),
                "BRL",
                PaymentMethod.PIX,
                "ORDER-1"
        )).isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Payment.create(
                BigDecimal.ONE,
                "REAL",
                PaymentMethod.PIX,
                "ORDER-1"
        )).isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Payment.create(
                BigDecimal.ONE,
                "BRL",
                PaymentMethod.PIX,
                "A".repeat(101)
        )).isInstanceOf(DomainException.class);
    }
}
