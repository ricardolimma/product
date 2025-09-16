package com.product.domain.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.domain.entity.ProductEntity;
import com.product.domain.repository.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(prefix = "app.storage", name = "layout", havingValue = "per-file", matchIfMissing = true)
public class ProductRepositoryImpl implements ProductRepository {

    private final Map<String, ProductEntity> cache = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public ProductRepositoryImpl(ObjectMapper mapper) {
        this.mapper = mapper;
        loadAllFromClasspath();
    }

    private void loadAllFromClasspath() {
        try {
            var resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
            Resource[] resources = resolver.getResources("classpath*:data/products/*.json");
            for (Resource res : resources) {
                String fileName = Objects.requireNonNull(res.getFilename());
                String productId = fileName.substring(0, fileName.length() - ".json".length());
                try (InputStream is = res.getInputStream()) {
                    ProductEntity p = mapper.readValue(is, ProductEntity.class);
                    cache.put(productId, p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar produtos", e);
        }
    }

    @Override
    public Optional<ProductEntity> findById(String productId) {
        return Optional.ofNullable(cache.get(productId));
    }

    @Override
    public List<ProductEntity> findAll() {
        return new ArrayList<>(cache.values());
    }
}
