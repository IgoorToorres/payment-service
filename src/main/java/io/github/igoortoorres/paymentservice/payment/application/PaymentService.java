package io.github.igoortoorres.paymentservice.payment.application;

import io.github.igoortoorres.paymentservice.payment.domain.Payment;
import io.github.igoortoorres.paymentservice.payment.domain.PaymentRepository;
import io.github.igoortoorres.paymentservice.payment.error.ResourceNotFound;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private PaymentRepository repository;

    public PaymentService(PaymentRepository repository){
        this.repository = repository;
    }

    public Payment create(CreatePaymentCommand command){
        Payment payment = new Payment(
                command.amount(),
                command.currency(),
                command.paymentMethod(),
                command.externalReference()
        );

        Payment savedPayment = repository.save(payment);

        return savedPayment;
    }

    public Payment findById(UUID id){
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("payment nao encontrado"));

        return payment;
    }

    public List<Payment> findAll(){
        List<Payment> payments = repository.findAll();
        return payments;
    }
}
