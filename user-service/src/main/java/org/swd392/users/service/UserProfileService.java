package org.swd392.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swd392.users.entity.User;
import org.swd392.users.entity.UserProfile;
import org.swd392.users.repository.UserProfileRepository;
import org.swd392.users.repository.UserRepository;
import org.swd392.users.dto.UserProfileDTO;


import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public UserProfile createOrUpdateProfile(Long userId, UserProfileDTO profileDetails) {
        // Lấy người dùng từ DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm hoặc tạo mới UserProfile
        UserProfile profile = profileRepository.findByUserId(userId).orElseGet(UserProfile::new);

        // Gán dữ liệu từ DTO
        profile.setUser(user);
        profile.setFullName(profileDetails.getFullName());
        profile.setBirthDay(profileDetails.getBirthDay());
        profile.setGender(profileDetails.getGender());
        profile.setPhoneNumber(profileDetails.getPhoneNumber());
        profile.setAddress(profileDetails.getAddress());
        profile.setImageUrl(profileDetails.getImageUrl());
        profile.setSchool(profileDetails.getSchool());
        profile.setAccountType(profileDetails.getAccountType());

        // Lưu vào DB
        return profileRepository.save(profile);
    }

    public UserProfile updateUserProfile(Long userId, UserProfileDTO profileDetails) {
        // Tương tự như createOrUpdateProfile
        return createOrUpdateProfile(userId, profileDetails);
    }

    public boolean deleteProfile(Long userId) {
        return profileRepository.findByUserId(userId).map(profile -> {
            profileRepository.delete(profile);
            return true;
        }).orElse(false);
    }

}
