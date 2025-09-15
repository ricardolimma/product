package com.product.domain.entity;

import com.product.contract.model.Reputation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerEntity {
    private String id;

    private String name;

    private Reputation reputation;
}
