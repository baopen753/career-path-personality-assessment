package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service") // The name of the User service that Seminar service (Consumer) needs to call
public interface UserFeignClient {

}
