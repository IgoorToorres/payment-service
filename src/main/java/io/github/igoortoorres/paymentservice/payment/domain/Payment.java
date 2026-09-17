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

    public Payment(
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String externalReference
        ){
        validateAmount(amount);
        validateCurrency(currency);
        validatePaymentMethod(paymentMethod);
        validateExternalReference(externalReference);

        this.id = UUID.randomUUID();
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.externalReference = externalReference;
        this.status = PaymentStatus.CREATED;
        this.createdAt = Instant.now();
    }

    private void validateAmount(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new DomainException("O valor da transação deve ser maior que zero");
        }
    }

    private void validateCurrency(String currency){
        if(currency == null || currency.isBlank()){
            throw new DomainException("Currency é obrigatório");
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod){
        if(paymentMethod == null){
            throw new DomainException("Metodo é obrigatorio");
        }
    }

    private void validateExternalReference(String externalReference){
        if(externalReference == null || externalReference.isBlank()){
            throw new DomainException("External reference é obrigatorio");
        }
    }

}
