package com.product.infra.exception;

import org.springframework.http.HttpStatus;
import java.time.OffsetDateTime;

public enum ProductError {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Produto não encontrado");

    private final HttpStatus status;
    private final String message;

    ProductError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }

    public ErrorResponse toResponse() {
        return new ErrorResponse(this.name(), this.message, OffsetDateTime.now());
    }
}
