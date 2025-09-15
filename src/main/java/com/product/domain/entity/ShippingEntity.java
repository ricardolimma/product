package com.product.domain.entity;

import com.product.contract.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEntity {
    private Boolean free;

    private Money cost;

    private String estimatedDelivery;
}
