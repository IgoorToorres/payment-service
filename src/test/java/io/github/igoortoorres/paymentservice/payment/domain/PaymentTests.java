package io.github.igoortoorres.paymentservice.payment.domain;

import io.github.igoortoorres.paymentservice.payment.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTests {

    private static final BigDecimal AMOUNT = new BigDecimal("199.90");
    private static final String CURRENCY = "BRL";
    private static final String EXTERNAL_REFERENCE = "ORDER-92831";

    @Test
    void shouldCreatePaymentWithInitialState() {
        Payment payment = Payment.create(
                AMOUNT,
                CURRENCY,
                PaymentMethod.PIX,
                EXTERNAL_REFERENCE
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

    @Test
    void shouldStartProcessingCreatedPayment() {
        Payment payment = paymentWithStatus(PaymentStatus.CREATED);

        payment.startProcessing();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PROCESSING);
    }

    @Test
    void shouldAuthorizeProcessingPayment() {
        Payment payment = paymentWithStatus(PaymentStatus.PROCESSING);

        payment.authorize();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void shouldDeclineProcessingPayment() {
        Payment payment = paymentWithStatus(PaymentStatus.PROCESSING);

        payment.decline();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.DECLINED);
    }

    @Test
    void shouldFailProcessingPayment() {
        Payment payment = paymentWithStatus(PaymentStatus.PROCESSING);

        payment.fail();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void shouldSettleAuthorizedPayment() {
        Payment payment = paymentWithStatus(PaymentStatus.AUTHORIZED);

        payment.settle();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "CREATED", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectStartProcessingFromInvalidStatus(PaymentStatus currentStatus) {
        assertInvalidTransition(
                currentStatus,
                PaymentStatus.PROCESSING,
                PaymentStatus.CREATED,
                Payment::startProcessing
        );
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "PROCESSING", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectAuthorizationFromInvalidStatus(PaymentStatus currentStatus) {
        assertInvalidTransition(
                currentStatus,
                PaymentStatus.AUTHORIZED,
                PaymentStatus.PROCESSING,
                Payment::authorize
        );
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "PROCESSING", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectDeclineFromInvalidStatus(PaymentStatus currentStatus) {
        assertInvalidTransition(
                currentStatus,
                PaymentStatus.DECLINED,
                PaymentStatus.PROCESSING,
                Payment::decline
        );
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "PROCESSING", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectFailureFromInvalidStatus(PaymentStatus currentStatus) {
        assertInvalidTransition(
                currentStatus,
                PaymentStatus.FAILED,
                PaymentStatus.PROCESSING,
                Payment::fail
        );
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "AUTHORIZED", mode = EnumSource.Mode.EXCLUDE)
    void shouldRejectSettlementFromInvalidStatus(PaymentStatus currentStatus) {
        assertInvalidTransition(
                currentStatus,
                PaymentStatus.SETTLED,
                PaymentStatus.AUTHORIZED,
                Payment::settle
        );
    }

    private void assertInvalidTransition(
            PaymentStatus currentStatus,
            PaymentStatus targetStatus,
            PaymentStatus expectedStatus,
            java.util.function.Consumer<Payment> transition
    ) {
        Payment payment = paymentWithStatus(currentStatus);

        assertThatThrownBy(() -> transition.accept(payment))
                .isInstanceOf(DomainException.class)
                .hasMessage(
                        "Transição inválida: pagamento no estado %s não pode mudar para %s; o estado esperado é %s",
                        currentStatus,
                        targetStatus,
                        expectedStatus
                );

        assertThat(payment.getStatus()).isEqualTo(currentStatus);
    }

    private Payment paymentWithStatus(PaymentStatus status) {
        return Payment.restore(
                UUID.randomUUID(),
                AMOUNT,
                CURRENCY,
                PaymentMethod.PIX,
                EXTERNAL_REFERENCE,
                status,
                Instant.now()
        );
    }
}
