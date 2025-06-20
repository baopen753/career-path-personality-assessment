package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user") // The name of the User service - get from application properties: application.name
public interface UserFeignClient {



}
