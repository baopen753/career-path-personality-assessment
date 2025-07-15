package org.swd392.seminars.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.event.PaymentCallbackEvent;

@Slf4j
@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class SagaCallbackController {

    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/payment-callback")
    public void handlePaymentCallback(@RequestBody PaymentCallbackEvent paymentCallbackEvent) {

        String paymentOrderCode = paymentCallbackEvent.getPaymentOrderCode();
        boolean isSuccess = paymentCallbackEvent.isSuccess();
        String message = paymentCallbackEvent.getMessage();

        log.info("Received payment callback - Order Code: {}, Success: {}, Message: {}",
                paymentOrderCode, isSuccess, message);

        // Publish event for saga orchestration
        eventPublisher.publishEvent(paymentCallbackEvent);
    }
} 