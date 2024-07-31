package com.earl.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
       Integer id,
       @NotNull(message = "Product name is required")
       String name,
       @NotNull(message = "Product description is required")
       String description,
       @Positive(message = "available quantity should be positive")
       double availableQuantity,
       @Positive(message = "price should be price")
       BigDecimal price,
       @NotNull(message = "product category is required")
       Integer categoryId
) {
}
