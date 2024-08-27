package com.earl.payment.payment;

import com.earl.payment.notification.NotificationProducer;
import com.earl.payment.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer  notificationProducer;


    public Integer createPayment(PaymentRequest request) {
        var payment = repository.save(mapper.toPayment(request));

        //send notification to kafka
        notificationProducer.sendNotification(new PaymentNotificationRequest(
                request.orderReference(),
                request.amount(),
                request.paymentMethod(),
                request.customer().firstname(),
                request.customer().lastname(),
                request.customer().email()
        ));
        return payment.getId();
    }
}
