package com.product.infra.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorResponse> handle(ProductException ex) {
        ProductError err = ex.getError();
        return ResponseEntity
                .status(err.getStatus())
                .body(err.toResponse());
    }
}
