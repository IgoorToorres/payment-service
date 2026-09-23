package io.github.igoortoorres.paymentservice.payment.api.mapper;

import io.github.igoortoorres.paymentservice.payment.api.dto.CreatePaymentRequest;
import io.github.igoortoorres.paymentservice.payment.api.dto.PaymentResponse;
import io.github.igoortoorres.paymentservice.payment.application.CreatePaymentCommand;
import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {
    public CreatePaymentCommand toCommand(
            CreatePaymentRequest request,
            String idempotencyKey
            ){
        return new CreatePaymentCommand(
                request.amount(),
                request.currency(),
                request.paymentMethod(),
                request.externalReference(),
                idempotencyKey
        );
    }

    public PaymentResponse toResponse(Payment payment){
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getExternalReference(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        return payments.stream()
                .map(this::toResponse)
                .toList();
    }
}
