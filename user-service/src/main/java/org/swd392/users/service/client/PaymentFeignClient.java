package org.swd392.users.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.swd392.users.dto.PaymentRequestDTO;
import org.swd392.users.dto.PaymentResponseDto;

@FeignClient(name = "payment")
public interface PaymentFeignClient {

    @PostMapping("/payment/api/payments/create")
    ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDTO request);

}
