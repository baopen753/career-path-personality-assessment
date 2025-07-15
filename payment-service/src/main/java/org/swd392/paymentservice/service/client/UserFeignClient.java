package org.swd392.paymentservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.swd392.paymentservice.dto.PaymentCallbackEvent;

@FeignClient(name = "user")
public interface UserFeignClient {

    @PostMapping("/user/api/users/payment-callback")
    void handlePaymentCallback(@RequestBody PaymentCallbackEvent paymentCallbackEvent);

}