package io.github.igoortoorres.paymentservice.payment.api.dto;

import io.github.igoortoorres.paymentservice.payment.domain.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados necessários para criar um pagamento")
public record CreatePaymentRequest(

        @Schema(description = "Valor do pagamento", example = "199.90")
        @NotNull(message = "O valor da transação é obrigatório")
        @Positive(message = "O valor da transação deve ser maior que zero")
        @Digits(
                integer = 17,
                fraction = 2,
                message = "O valor deve possuir no máximo 17 dígitos inteiros e duas casas decimais"
        )
        BigDecimal amount,

        @Schema(description = "Código ISO da moeda", example = "BRL")
        @NotBlank(message = "A moeda é obrigatória")
        @Size(min = 3, max = 3, message = "A moeda deve possuir exatamente 3 caracteres")
        String currency,

        @Schema(description = "Método de pagamento", example = "PIX")
        @NotNull(message = "O método de pagamento é obrigatório")
        PaymentMethod paymentMethod,

        @Schema(description = "Referência do pedido no sistema cliente", example = "ORDER-92831")
        @NotBlank(message = "A referência externa é obrigatória")
        @Size(max = 100, message = "A referência externa deve possuir no máximo 100 caracteres")
        String externalReference
) {
}
