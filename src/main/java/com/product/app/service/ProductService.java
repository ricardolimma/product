package com.product.app.service;

import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;

import java.util.List;

public interface ProductService {
    Product getProduct(String productId);

    ProductPage listProducts(Integer page, Integer size);
}
