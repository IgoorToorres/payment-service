package io.github.igoortoorres.paymentservice.payment.infrastructure.persistence;

import io.github.igoortoorres.paymentservice.TestcontainersConfiguration;
import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class JpaPaymentRepositoryAdapterIntegrationTests {

    private static final String IDEMPOTENCY_KEY = "atomic-payment-key";

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private SpringDataPaymentRepository jpaRepository;

    @Test
    void shouldInsertOnlyOnceForSameIdempotencyKey() {
        Payment firstPayment = newPayment();
        Payment repeatedPayment = newPayment();

        boolean firstInserted = repository.saveIfIdempotencyKeyAbsent(firstPayment);
        boolean repeatedInserted = repository.saveIfIdempotencyKeyAbsent(repeatedPayment);

        assertThat(firstInserted).isTrue();
        assertThat(repeatedInserted).isFalse();
        assertThat(jpaRepository.countByIdempotencyKey(IDEMPOTENCY_KEY)).isEqualTo(1);
        assertThat(repository.findByIdempotencyKey(IDEMPOTENCY_KEY))
                .get()
                .extracting(Payment::getId)
                .isEqualTo(firstPayment.getId());
    }

    private Payment newPayment() {
        return Payment.create(
                new BigDecimal("199.90"),
                "BRL",
                PaymentMethod.PIX,
                "ORDER-92831",
                IDEMPOTENCY_KEY
        );
    }
}
