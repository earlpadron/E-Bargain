package com.earl.order.kafka;

import com.earl.order.PaymentMethod;
import com.earl.order.customer.CustomerResponse;
import com.earl.order.product.PurchaseResponse;

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
