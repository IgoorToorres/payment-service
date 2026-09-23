package io.github.igoortoorres.paymentservice.payment.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestHeader(
            MissingRequestHeaderException exception,
            HttpServletRequest request
    ) {
        String message = "Idempotency-Key".equals(exception.getHeaderName())
                ? "O header Idempotency-Key é obrigatório"
                : "O header %s é obrigatório".formatted(exception.getHeaderName());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                message,
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Falha na validação dos campos",
                request.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(
            DomainException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFound exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not found",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleIdempotencyConflict(
            IdempotencyConflictException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<FieldErrorResponse> fieldErrors
    ){
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path,
                fieldErrors
        );

        return ResponseEntity.status(status).body(response);
    }

}
