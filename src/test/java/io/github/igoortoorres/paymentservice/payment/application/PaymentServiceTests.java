package io.github.igoortoorres.paymentservice.payment.application;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentStatus;
import io.github.igoortoorres.paymentservice.payment.error.IdempotencyConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    private static final String IDEMPOTENCY_KEY = "payment-order-92831";

    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentService service;

    @Test
    void shouldCreatePaymentWhenIdempotencyKeyDoesNotExist() {
        CreatePaymentCommand command = validCommand();
        when(repository.findByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(repository.saveIfIdempotencyKeyAbsent(any(Payment.class))).thenReturn(true);

        CreatePaymentResult result = service.create(command);

        assertThat(result.created()).isTrue();
        assertThat(result.payment().getIdempotencyKey()).isEqualTo(IDEMPOTENCY_KEY);
        verify(repository).saveIfIdempotencyKeyAbsent(result.payment());
    }

    @Test
    void shouldReturnExistingPaymentForRepeatedRequest() {
        Payment existing = existingPayment();
        when(repository.findByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.of(existing));

        CreatePaymentResult result = service.create(validCommand());

        assertThat(result.created()).isFalse();
        assertThat(result.payment()).isSameAs(existing);
        verify(repository, never()).saveIfIdempotencyKeyAbsent(any(Payment.class));
    }

    @ParameterizedTest
    @MethodSource("commandsWithDifferentCreationData")
    void shouldRejectIdempotencyKeyReusedWithDifferentData(CreatePaymentCommand command) {
        when(repository.findByIdempotencyKey(IDEMPOTENCY_KEY))
                .thenReturn(Optional.of(existingPayment()));

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(IdempotencyConflictException.class)
                .hasMessage("A chave de idempotência já foi utilizada com dados diferentes");

        verify(repository, never()).saveIfIdempotencyKeyAbsent(any(Payment.class));
    }

    @Test
    void shouldReturnConcurrentPaymentWhenAnotherRequestCreatesFirst() {
        Payment existing = existingPayment();
        when(repository.findByIdempotencyKey(IDEMPOTENCY_KEY))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existing));
        when(repository.saveIfIdempotencyKeyAbsent(any(Payment.class))).thenReturn(false);

        CreatePaymentResult result = service.create(validCommand());

        assertThat(result.created()).isFalse();
        assertThat(result.payment()).isSameAs(existing);
    }

    private static Stream<CreatePaymentCommand> commandsWithDifferentCreationData() {
        return Stream.of(
                new CreatePaymentCommand(
                        new BigDecimal("200.00"), "BRL", PaymentMethod.PIX, "ORDER-92831", IDEMPOTENCY_KEY
                ),
                new CreatePaymentCommand(
                        new BigDecimal("199.90"), "USD", PaymentMethod.PIX, "ORDER-92831", IDEMPOTENCY_KEY
                ),
                new CreatePaymentCommand(
                        new BigDecimal("199.90"), "BRL", PaymentMethod.CREDIT_CARD, "ORDER-92831", IDEMPOTENCY_KEY
                ),
                new CreatePaymentCommand(
                        new BigDecimal("199.90"), "BRL", PaymentMethod.PIX, "ORDER-OTHER", IDEMPOTENCY_KEY
                )
        );
    }

    private CreatePaymentCommand validCommand() {
        return new CreatePaymentCommand(
                new BigDecimal("199.90"),
                "BRL",
                PaymentMethod.PIX,
                "ORDER-92831",
                IDEMPOTENCY_KEY
        );
    }

    private Payment existingPayment() {
        return Payment.restore(
                UUID.randomUUID(),
                new BigDecimal("199.90"),
                "BRL",
                PaymentMethod.PIX,
                "ORDER-92831",
                IDEMPOTENCY_KEY,
                PaymentStatus.CREATED,
                Instant.now()
        );
    }
}
