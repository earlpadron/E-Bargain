package com.earl.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductPurchaseRequest(
        @NotNull(message = "Product is mandatory")
        Integer productId,
        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than 0")
        double quantity
) {
}
