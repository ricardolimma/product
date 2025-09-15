package com.product.app.controller;

import com.product.contract.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    public ResponseEntity<Product> getProductById(@PathVariable String productId) {

        return null;
    }
}
