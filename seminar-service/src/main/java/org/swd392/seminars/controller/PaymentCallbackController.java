package org.swd392.seminars.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.seminars.event.PaymentCallbackEvent;

@Slf4j
@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/payment-callback")
    public ResponseEntity<String> handlePaymentCallback(@RequestBody PaymentCallbackEvent paymentCallbackEvent) {

        Long paymentOrderCode = paymentCallbackEvent.getPaymentOrderCode();
        boolean isSuccess = paymentCallbackEvent.isSuccess();
        String message = paymentCallbackEvent.getMessage();

        log.info("🎯 Received payment callback - Order Code: {}, Success: {}, Message: {}",
                paymentOrderCode, isSuccess, message);

        try {
            // Publish event for saga orchestration
            eventPublisher.publishEvent(paymentCallbackEvent);
            log.info("✅ Published payment callback event for order code: {}", paymentOrderCode);
            return ResponseEntity.ok("Payment callback processed successfully");
        } catch (Exception e) {
            log.error("❌ Failed to process payment callback for order code: {}", paymentOrderCode, e);
            return ResponseEntity.internalServerError().body("Failed to process payment callback");
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment callback controller is healthy");
    }
}
