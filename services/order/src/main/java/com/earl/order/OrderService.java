package com.earl.order;

import com.earl.order.customer.CustomerClient;
import com.earl.order.exception.BusinessException;
import com.earl.order.orderline.OrderLineRequest;
import com.earl.order.orderline.OrderLineService;
import com.earl.order.product.ProductClient;
import com.earl.order.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest request){
        //check customer exists --> openfeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists with provided id::" + request.customerId()));

        //purchase the products -- access product-ms
        this.productClient.purchasedProducts(request.products());

        //persist order into database
        var savedOrder = this.repository.save(mapper.ToOrder(request));

        //persist orderlines
        for(PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            savedOrder.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //TODO:start payment process

        //TODO:send order confirmation --> notification-ms (kafka)

        return null;

    }

}
