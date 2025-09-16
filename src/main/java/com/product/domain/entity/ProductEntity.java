package com.product.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    private String productId;

    private String title;

    private PriceEntity price;

    private List<URI> images = new ArrayList<>();

    private String description;

    private List<AttributeEntity> attributes = new ArrayList<>();

    private CategoryEntity category;

    private SellerEntity seller;

    private StockEntity stock;

    private ShippingEntity shipping;

    private ReviewsEntity reviews;
}
