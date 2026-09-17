package io.github.igoortoorres.paymentservice.payment.api;

import io.github.igoortoorres.paymentservice.payment.api.dto.CreatePaymentRequest;
import io.github.igoortoorres.paymentservice.payment.api.dto.PaymentResponse;
import io.github.igoortoorres.paymentservice.payment.api.mapper.PaymentMapper;
import io.github.igoortoorres.paymentservice.payment.application.CreatePaymentCommand;
import io.github.igoortoorres.paymentservice.payment.application.PaymentService;
import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private PaymentService paymentService;
    private PaymentMapper mapper;

    public PaymentController(PaymentService paymentService, PaymentMapper mapper){
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request){
        CreatePaymentCommand command = mapper.toCommand(request);
        Payment payment = paymentService.create(command);
        PaymentResponse response =  mapper.toResponse(payment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID id){
        Payment payment = paymentService.findById(id);
        PaymentResponse response = mapper.toResponse(payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAll(){
        List<Payment> payments = paymentService.findAll();
        List<PaymentResponse> response = mapper.toResponseList(payments);
        return ResponseEntity.ok(response);
    }
}
