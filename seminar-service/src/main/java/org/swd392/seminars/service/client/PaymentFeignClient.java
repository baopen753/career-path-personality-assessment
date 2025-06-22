package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.swd392.seminars.dto.PaymentRequestDTO;
import org.swd392.seminars.dto.PaymentResponseDto;

@FeignClient(name = "payment")
public interface PaymentFeignClient {

    @PostMapping("/payment/api/payments/create")
    ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDTO request);

}
