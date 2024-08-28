package com.earl.ecommerce.payment;


import com.earl.ecommerce.customer.CustomerResponse;
import com.earl.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
