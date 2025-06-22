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
    public void handlePaymentCallback(@RequestParam("paymentOrderCode") String paymentOrderCode,
                                     @RequestParam("success") boolean success,
                                     @RequestParam("message") String message) {
        
        log.info("Received payment callback - Order Code: {}, Success: {}, Message: {}", 
                paymentOrderCode, success, message);
        
        // Publish event for saga orchestration
        PaymentCallbackEvent event = new PaymentCallbackEvent(paymentOrderCode, success, message);
        eventPublisher.publishEvent(event);
    }
} 