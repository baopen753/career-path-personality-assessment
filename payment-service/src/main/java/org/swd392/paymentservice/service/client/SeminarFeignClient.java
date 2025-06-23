package org.swd392.paymentservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.swd392.paymentservice.dto.PaymentCallbackEvent;

@FeignClient(name = "seminar")
public interface SeminarFeignClient {

    @PostMapping("/seminar/api/saga/payment-callback")
    void handlePaymentCallback(@RequestBody PaymentCallbackEvent paymentCallbackEvent);
} 