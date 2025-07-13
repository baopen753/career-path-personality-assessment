package org.swd392.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swd392.users.event.PaymentCallbackEvent;


@Slf4j
@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class SagaCallbackController {

    @PostMapping("/payment-callback")
    public void handlePaymentCallback(@RequestBody PaymentCallbackEvent paymentCallbackEvent) {

        String paymentOrderCode = paymentCallbackEvent.getPaymentOrderCode();
        boolean isSuccess = paymentCallbackEvent.isSuccess();
        String message = paymentCallbackEvent.getMessage();

        log.info("Received payment callback - Order Code: {}, Success: {}, Message: {}",
                paymentOrderCode, isSuccess, message);


    }
} 