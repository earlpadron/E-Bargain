package com.earl.ecommerce.customer;

import com.earl.ecommerce.Address;

public record CustomerResponse(
        String id,
        String firstname,
        String lastname,
        String email,
        Address address
) {
}
