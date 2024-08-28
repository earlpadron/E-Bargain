package com.earl.ecommerce.order;

import com.earl.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Payment method should be valid")
        PaymentMethod paymentMethod,
        @NotEmpty(message = "Customer should be present")
        @NotBlank(message = "Customer should be present")
        @NotNull(message = "Customer should be present")
        String customerId,
        @NotEmpty(message = "products cannot be empty, please purchase a product")
        List<PurchaseRequest> products


) {
}
