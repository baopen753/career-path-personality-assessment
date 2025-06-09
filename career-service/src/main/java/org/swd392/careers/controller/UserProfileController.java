package org.swp392.users.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp392.users.dto.UserProfileDTO;
import org.swp392.users.entity.UserProfile;
import org.swp392.users.mapper.UserProfileMapper;
import org.swp392.users.service.UserProfileService;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService service;

    // Lấy hồ sơ người dùng theo userId
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return service.getProfileByUserId(userId)
                .map(profile -> ResponseEntity.ok(UserProfileMapper.toDTO(profile)))  // Trả về profileId
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo mới hoặc cập nhật hồ sơ người dùng
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> createOrUpdateProfile(@PathVariable Long userId, @RequestBody UserProfileDTO userProfileDTO) {
        try {
            // Tạo hoặc cập nhật hồ sơ người dùng
            UserProfile profile = service.createOrUpdateProfile(userId, userProfileDTO);

            // Chuyển đổi thành DTO để trả về
            UserProfileDTO profileDTO = UserProfileMapper.toDTO(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(profileDTO); // Trả về HTTP 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Trả về HTTP 404 nếu không tìm thấy người dùng
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfileDTO userProfileDTO) {
        try {
            // Cập nhật hoặc tạo mới hồ sơ người dùng
            UserProfile updatedProfile = service.updateUserProfile(userId, userProfileDTO);

            // Chuyển đổi UserProfile sang UserProfileDTO để trả về
            UserProfileDTO profileDTO = UserProfileMapper.toDTO(updatedProfile);
            return ResponseEntity.status(HttpStatus.OK).body(profileDTO); // Trả về HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Trả về HTTP 404 nếu không tìm thấy người dùng
        }
    }

    // Xóa hồ sơ người dùng
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        boolean deleted = service.deleteProfile(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}