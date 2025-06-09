package org.swd392.paymentservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service") // The name of the User service that Payment service (Consumer) needs to call
public interface UserFeignClient {

}
