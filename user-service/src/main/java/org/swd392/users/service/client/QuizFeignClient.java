package org.swd392.users.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "quiz-service") // The name of the Quiz service that User service (Consumer) needs to call
public interface QuizFeignClient {

}
