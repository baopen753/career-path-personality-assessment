package org.swd392.users.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification", url = "${notification.service.url}")
public interface NotificationFeignClient {

    @PostMapping("/api/notifications/account-registration")
    ResponseEntity<Void> sendAccountRegistrationConfirmation(
            @RequestParam String userEmail,
            @RequestParam String userName,
            @RequestParam String username,
            @RequestParam String accountType,
            @RequestParam String registrationDate,
            @RequestParam String loginLink);

}