package org.swd392.users.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "seminar-service") // The name of the Seminar service that User service (Consumer) needs to call
public interface SeminarFeignClient {

}
