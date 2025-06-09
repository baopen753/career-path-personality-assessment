package org.swd392.careers.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "quiz-service") // The name of the Quiz service that Career service (Consumer) needs to call
public interface QuizFeignClient {

}
