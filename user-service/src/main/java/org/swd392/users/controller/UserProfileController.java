package org.swd392.users.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.ApiResponse;
import org.swd392.users.dto.UserDto;
import org.swd392.users.dto.UserProfileDto;
import org.swd392.users.entity.User;
import org.swd392.users.entity.UserProfile;
import org.swd392.users.service.UserProfileService;
import org.swd392.users.service.impl.IUserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private IUserService userService;


    @PostMapping("/{userId}")
    public ResponseEntity<UserProfile> createOrUpdateProfile(@PathVariable Long userId, @RequestBody UserProfileDto profileDetails) {
        try {
            UserProfile profile = userProfileService.createOrUpdateProfile(userId, profileDetails);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        boolean deleted = userProfileService.deleteProfile(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@PathVariable Long id) {
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
                .accountType(userProfile.getAccountType())
                .gender(userProfile.getGender())
                .build();

        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .code(200)
                .message("User retrieved successfully")
                .result(userProfileDto)
                .build());
    }
}