package com.product.app.controller;

import com.product.app.service.ProductService;
import com.product.contract.api.ProductApiDelegate;
import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductController implements ProductApiDelegate {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    public ResponseEntity<Product> getProductById(String productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    public ResponseEntity<ProductPage> listProducts(Integer page, Integer size) {
        return ResponseEntity.ok(productService.listProducts(page, size));
    }
}
