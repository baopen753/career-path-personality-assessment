package org.swd392.users.mapper;

import org.swd392.users.dto.UserProfileDto;
import org.swd392.users.entity.UserProfile;

public class UserProfileMapper {

    // Chuyển từ UserProfile entity sang UserProfileDTO
    public static UserProfileDto toDTO(UserProfile userProfile) {
        return new UserProfileDto(
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
    public static UserProfile toEntity(UserProfileDto dto) {
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
