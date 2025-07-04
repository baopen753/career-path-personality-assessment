package org.swd392.seminars.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.swd392.seminars.dto.ApiResponse;
import org.swd392.seminars.dto.UserInfoDto;

@FeignClient(name = "user") // The name of the User service - get from application properties: application.name
public interface UserFeignClient {

    @GetMapping("user/api/profiles/internal/{id}")
    ResponseEntity<ApiResponse<UserInfoDto>> getUserDetails(@PathVariable Long id);

}
