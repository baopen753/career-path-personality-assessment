package org.swp392.users.mapper;

import org.swp392.users.dto.UserProfileDTO;
import org.swp392.users.entity.UserProfile;

public class UserProfileMapper {

    // Chuyển từ UserProfile entity sang UserProfileDTO
    public static UserProfileDTO toDTO(UserProfile userProfile) {
        return new UserProfileDTO(
                userProfile.getUser().getId(),        // userId
                userProfile.getId(),                  // profileId
                userProfile.getFullName(),
                userProfile.getBirthDay(),
                userProfile.getPhoneNumber(),
                userProfile.getAddress(),
                userProfile.getImageUrl(),
                userProfile.getSchool(),
                userProfile.getAccountType(),
                userProfile.getGender()
        );
    }

    // Chuyển từ UserProfileDTO sang UserProfile entity
    public static UserProfile toEntity(UserProfileDTO dto) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(dto.getProfileId());
        userProfile.setFullName(dto.getFullName());
        userProfile.setBirthDay(dto.getBirthDay());
        userProfile.setPhoneNumber(dto.getPhoneNumber());
        userProfile.setAddress(dto.getAddress());
        userProfile.setImageUrl(dto.getImageUrl());
        userProfile.setSchool(dto.getSchool());
        userProfile.setAccountType(dto.getAccountType());
        userProfile.setGender(dto.getGender());
        // Lưu ý: user cần được set riêng khi gọi hàm toEntity
        return userProfile;
    }
}
