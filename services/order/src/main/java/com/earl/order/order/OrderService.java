package com.earl.order.order;

import com.earl.order.customer.CustomerClient;
import com.earl.order.exception.BusinessException;
import com.earl.order.kafka.OrderConfirmation;
import com.earl.order.kafka.OrderProducer;
import com.earl.order.orderline.OrderLineRequest;
import com.earl.order.orderline.OrderLineService;
import com.earl.order.payment.PaymentClient;

import com.earl.order.payment.PaymentRequest;
import com.earl.order.product.ProductClient;
import com.earl.order.product.PurchaseRequest;
import com.earl.order.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request){
        //check customer exists --> openfeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists with provided id::" + request.customerId()));

        //purchase the products -- access product-ms
        List<PurchaseResponse> purchasedProducts = this.productClient.purchasedProducts(request.products());

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
        var paymentRequest = new PaymentRequest( //2 different PaymentRequest types, can cause an issue
                request.amount(),
                request.paymentMethod(),
                savedOrder.getId(),
                savedOrder.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);


        //TODO:send order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts)
        );

        return savedOrder.getId();

    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with ID: %d", orderId)));
    }
}
