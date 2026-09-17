package io.github.igoortoorres.paymentservice.payment.api.dto;

import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(

        @NotNull(message = "O valor da transação é obrigatório")
        @Positive(message = "O valor da transação deve ser maior que zero")
        @Digits(
                integer = 17,
                fraction = 2,
                message = "O valor deve possuir no máximo duas casas decimais"
        )
        BigDecimal amount,

        @NotBlank(message = "Currency é obrigatorio")
        String currency,

        @NotNull(message = "meotod é obrigatorio")
        PaymentMethod paymentMethod,

        @NotBlank(message = "External reference é obrigatorio")
        String externalReference
) {
}
