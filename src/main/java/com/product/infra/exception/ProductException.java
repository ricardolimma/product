package com.product.infra.exception;

public class ProductException extends RuntimeException {

    private final ProductError error;

    public ProductException(ProductError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ProductError getError() {
        return error;
    }
}
