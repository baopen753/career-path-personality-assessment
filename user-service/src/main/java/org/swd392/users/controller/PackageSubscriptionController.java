package org.swd392.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.PaymentInitiationResponse;
import org.swd392.users.dto.ResponseDTO;
import org.swd392.users.service.ISubscriptionService;

@RestController
@RequestMapping("/api/package")
public class PackageSubscriptionController {

    private final ISubscriptionService subscriptionService;

    public PackageSubscriptionController(ISubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PreAuthorize("#userId == authentication.principal.id")
    @PostMapping("/upgrade/{userId}")
    public ResponseEntity<ResponseDTO<PaymentInitiationResponse>> upgrade(@PathVariable Long userId) {

        PaymentInitiationResponse responseDto = subscriptionService.upgrade(userId);

        return ResponseEntity.ok(
                ResponseDTO.<PaymentInitiationResponse>builder()
                        .status(200)
                        .message("Create payment link for upgrading subscription successfully")
                        .data(responseDto)
                        .build()
        );
    }
}
