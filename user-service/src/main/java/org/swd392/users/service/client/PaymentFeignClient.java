package org.swd392.users.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service") // The name of the User service that Payment service (Consumer) needs to call
public interface PaymentFeignClient {

}
