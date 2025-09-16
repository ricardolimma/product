package com.product.app.service.impl;

import com.product.app.mapper.ProductMapper;
import com.product.app.service.ProductService;
import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;
import com.product.domain.entity.ProductEntity;
import com.product.domain.repository.ProductRepository;
import com.product.infra.exception.ProductError;
import com.product.infra.exception.ProductException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductMapper productMapper;
    private ProductRepository productRepository;

    public ProductServiceImpl(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public Product getProduct(String productId) {
        Optional<ProductEntity> productEntity = productRepository.findById(productId);
        if (productEntity.isEmpty()) {
            throw new ProductException(ProductError.PRODUCT_NOT_FOUND);
        }

        return productMapper.toProduct(productEntity.get());
    }

    @Override
    public ProductPage listProducts(Integer page, Integer size) {
            int p = page == null ? 0 : Math.max(0, page);
            int s = size == null ? 10 : Math.max(1, size);

            Pageable pageable = PageRequest.of(p, s);

            List<ProductEntity> all = productRepository.findAll();
            int from = (int) pageable.getOffset();
            int to   = Math.min(from + pageable.getPageSize(), all.size());
            var slice = (from >= all.size()) ? List.<ProductEntity>of() : all.subList(from, to);

            Page<ProductEntity> pageEntity = new PageImpl<>(slice, pageable, all.size());

            List<Product> items = productMapper.toDTOList(pageEntity.getContent());

            return productMapper.toProductPage(items,
                    pageEntity.getNumber(),
                    pageEntity.getSize(),
                    pageEntity.getTotalElements());
        }
}
