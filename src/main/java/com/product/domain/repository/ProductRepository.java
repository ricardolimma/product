package com.product.domain.repository;

import com.product.domain.entity.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<ProductEntity> findById(String productId);

    List<ProductEntity> findAll();
}
