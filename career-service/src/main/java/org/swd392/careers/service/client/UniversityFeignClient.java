package org.swd392.careers.service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "university-service") // The name of the University service that Career service (Consumer) needs to call
public interface UniversityFeignClient {

}
