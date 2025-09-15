package com.product.app.mapper;

import com.product.contract.model.Product;
import com.product.domain.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toDTO(ProductEntity productEntity);
}
