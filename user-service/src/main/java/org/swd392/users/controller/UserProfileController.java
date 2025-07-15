package org.swd392.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.ApiResponse;
import org.swd392.users.dto.UserInfoDto;
import org.swd392.users.dto.UserProfileDto;
import org.swd392.users.entity.User;
import org.swd392.users.entity.UserProfile;
import org.swd392.users.exception.UserNotFoundException;
import org.swd392.users.service.UserProfileService;
import org.swd392.users.service.impl.IUserService;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private IUserService userService;

    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #userId == authentication.principal.id))")
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfile> createOrUpdateProfile(@PathVariable Long userId, @RequestBody UserProfileDto profileDetails) {
        try {
            UserProfile profile = userProfileService.createOrUpdateProfile(userId, profileDetails);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        boolean deleted = userProfileService.deleteProfile(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #id == authentication.principal.id))")
    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal class: " + auth.getPrincipal().toString());

        Optional<UserProfile> userProfileOpt = userProfileService.getProfileByUserId(id);
        if (userProfileOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<UserProfileDto>builder()
                            .code(404)
                            .message("User not found")
                            .build());
        }

        UserProfile userProfile = userProfileOpt.get();
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .userId(userProfile.getUser().getId())
                .profileId(userProfile.getId())
                .fullName(userProfile.getFullName())
                .birthDay(userProfile.getBirthDay())
                .phoneNumber(userProfile.getPhoneNumber())
                .address(userProfile.getAddress())
                .school(userProfile.getSchool())
                .gender(userProfile.getGender())
                .build();

        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .code(200)
                .message("User retrieved successfully")
                .result(userProfileDto)
                .build());
    }


    @GetMapping("/internal/{id}")
    public ResponseEntity<ApiResponse<UserInfoDto>> getUserDetails(@PathVariable Long id) {

        User userInDb = userService.getUserById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id)
        );

        UserInfoDto.UserInfoDtoBuilder userInfoDtoBuilder = UserInfoDto.builder();

        userInfoDtoBuilder
                .userId(userInDb.getId())
                .email(userInDb.getEmail())
                .build();

        Optional<UserProfile> userProfileOpt = userProfileService.getProfileByUserId(id);
        userProfileOpt.ifPresent(
                userProfile -> userInfoDtoBuilder
                        .fullName(userProfile.getFullName())
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.<UserInfoDto>builder()
                .code(200)
                .message("User retrieved successfully")
                .result(userInfoDtoBuilder.build())
                .build());
    }
}