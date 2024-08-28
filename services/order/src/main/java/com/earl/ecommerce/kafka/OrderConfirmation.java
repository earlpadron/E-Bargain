package com.earl.ecommerce.kafka;

import com.earl.ecommerce.order.PaymentMethod;
import com.earl.ecommerce.customer.CustomerResponse;
import com.earl.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
