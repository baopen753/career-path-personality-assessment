package org.swd392.quizzes.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "career-service") // The name of the Career service that Quiz service (Consumer) needs to call
public interface CareerFeignClient {

}
