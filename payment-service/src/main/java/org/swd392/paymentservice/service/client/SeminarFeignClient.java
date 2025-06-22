package org.swd392.paymentservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seminar")
public interface SeminarFeignClient {

    @PostMapping("/seminar/api/saga/payment-callback")
    void handlePaymentCallback(@RequestParam("paymentOrderCode") String paymentOrderCode,
                              @RequestParam("success") boolean success,
                              @RequestParam("message") String message);
} 