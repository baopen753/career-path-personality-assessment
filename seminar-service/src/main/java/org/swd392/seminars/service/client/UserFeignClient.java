package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service", 
    url = "http://localhost:8080/user",
    configuration = org.swd392.seminars.config.FeignConfig.class,
    fallback = UserFeignClientFallback.class
)
public interface UserFeignClient {

    @GetMapping("/api/users/{userId}/email")
    String getUserEmail(@PathVariable("userId") Integer userId);

    @GetMapping("/api/users/{userId}/name")
    String getUserName(@PathVariable("userId") Integer userId);

    @GetMapping("/api/users/{userId}/role")
    String getUserRole(@PathVariable("userId") Integer userId);

    @GetMapping("/api/users/{userId}/info")
    Object getUserInfo(@PathVariable("userId") Integer userId);
}
