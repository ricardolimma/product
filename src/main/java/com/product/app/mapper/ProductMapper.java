package com.product.app.mapper;

import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;
import com.product.domain.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductEntity productEntity);

    List<Product> toDTOList(List<ProductEntity> entities);

    @Mapping(target = "items", source = "products")
    @Mapping(target = "page",  source = "page")
    @Mapping(target = "size",  source = "size")
    @Mapping(target = "totalItems", source = "totalItems")
    @Mapping(target = "totalPages", expression = "java(calcTotalPages(totalItems, size))")
    @Mapping(target = "hasNext", expression = "java(hasNext(page, totalItems, size))")
    @Mapping(target = "hasPrevious", expression = "java(page > 0)")
    ProductPage toProductPage(
            List<Product> products,
            int page,
            int size,
            long totalItems
    );

    default int calcTotalPages(long totalItems, int size) {
        if (size <= 0) return 1;
        long pages = (totalItems + size - 1) / size;
        return (int) Math.max(pages, 1);
    }

    default boolean hasNext(int page, long totalItems, int size) {
        int totalPages = calcTotalPages(totalItems, size);
        return page < (totalPages - 1);
    }
}

