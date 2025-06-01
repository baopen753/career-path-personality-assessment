package org.swd392.quizzes.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service") // The name of the User service that Quiz service (Consumer) needs to call
public interface UserFeignClient {

}
