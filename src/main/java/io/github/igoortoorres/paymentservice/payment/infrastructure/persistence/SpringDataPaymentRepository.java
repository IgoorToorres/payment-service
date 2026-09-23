package io.github.igoortoorres.paymentservice.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    Optional<PaymentJpaEntity> findByIdempotencyKey(String idempotencyKey);

    long countByIdempotencyKey(String idempotencyKey);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = """
            INSERT INTO payments (
                id,
                amount,
                currency,
                payment_method,
                external_reference,
                idempotency_key,
                status,
                created_at
            ) VALUES (
                :id,
                :amount,
                :currency,
                :paymentMethod,
                :externalReference,
                :idempotencyKey,
                :status,
                :createdAt
            )
            ON CONFLICT (idempotency_key) DO NOTHING
            """, nativeQuery = true)
    int insertIfIdempotencyKeyAbsent(
            @Param("id") UUID id,
            @Param("amount") BigDecimal amount,
            @Param("currency") String currency,
            @Param("paymentMethod") String paymentMethod,
            @Param("externalReference") String externalReference,
            @Param("idempotencyKey") String idempotencyKey,
            @Param("status") String status,
            @Param("createdAt") Instant createdAt
    );
}
