package org.swd392.users.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.ApiResponse;
import org.swd392.users.dto.UserDto;
import org.swd392.users.dto.UserProfileDTO;
import org.swd392.users.entity.User;
import org.swd392.users.entity.UserProfile;
import org.swd392.users.service.UserProfileService;
import org.swd392.users.service.UserService;
import org.swd392.users.service.impl.IUserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService service;

    @Autowired
    private IUserService userService;


    @PostMapping("/{userId}")
    public ResponseEntity<UserProfile> createOrUpdateProfile(@PathVariable Long userId, @RequestBody UserProfileDTO profileDetails) {
        try {
            UserProfile profile = service.createOrUpdateProfile(userId, profileDetails);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        boolean deleted = service.deleteProfile(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('ADMIN') or (hasAnyRole('STUDENT','PARENT','EVENT_MANAGER') and #id == authentication.principal.userId)")
    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getAccount(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<UserDto>builder()
                            .code(404)
                            .message("User not found")
                            .build());
        }

        User user = userOpt.get();
        UserDto UserDto = org.swd392.users.dto.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .build();

        return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                .code(200)
                .message("User retrieved successfully")
                .result(UserDto)
                .build());
    }
}