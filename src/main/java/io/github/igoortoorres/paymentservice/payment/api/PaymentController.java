package io.github.igoortoorres.paymentservice.payment.api;

import io.github.igoortoorres.paymentservice.payment.api.dto.CreatePaymentRequest;
import io.github.igoortoorres.paymentservice.payment.api.dto.PaymentResponse;
import io.github.igoortoorres.paymentservice.payment.api.mapper.PaymentMapper;
import io.github.igoortoorres.paymentservice.payment.application.CreatePaymentCommand;
import io.github.igoortoorres.paymentservice.payment.application.CreatePaymentResult;
import io.github.igoortoorres.paymentservice.payment.application.PaymentService;
import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Operações para criação e consulta de pagamentos")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper mapper;

    public PaymentController(PaymentService paymentService, PaymentMapper mapper){
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Criar pagamento", description = "Cria um pagamento com status inicial CREATED")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pagamento criado",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento já existente retornado por idempotência",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do pagamento inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Chave de idempotência reutilizada com dados diferentes",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<PaymentResponse> create(
            @Parameter(
                    description = "Chave única que impede a criação duplicada do pagamento",
                    required = true,
                    example = "payment-order-92831"
            )
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        CreatePaymentCommand command = mapper.toCommand(request, idempotencyKey);
        CreatePaymentResult result = paymentService.create(command);
        PaymentResponse response = mapper.toResponse(result.payment());

        return ResponseEntity
                .status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento", description = "Busca um pagamento pelo identificador")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento encontrado",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pagamento não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<PaymentResponse> findById(
            @Parameter(description = "Identificador do pagamento")
            @PathVariable UUID id
    ){
        Payment payment = paymentService.findById(id);
        PaymentResponse response = mapper.toResponse(payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar pagamentos", description = "Lista todos os pagamentos cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Pagamentos encontrados",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))
    )
    public ResponseEntity<List<PaymentResponse>> findAll(){
        List<Payment> payments = paymentService.findAll();
        List<PaymentResponse> response = mapper.toResponseList(payments);
        return ResponseEntity.ok(response);
    }
}
