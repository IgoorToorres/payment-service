package io.github.igoortoorres.paymentservice.payment.infrastructure.persistence;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    public PaymentJpaEntity toJpaEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getExternalReference(),
                payment.getIdempotencyKey(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    public Payment toDomain(PaymentJpaEntity entity) {
        return Payment.restore(
                entity.getId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getPaymentMethod(),
                entity.getExternalReference(),
                entity.getIdempotencyKey(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
