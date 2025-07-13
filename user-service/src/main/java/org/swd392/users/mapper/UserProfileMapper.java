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
                userProfile.getGender()
        );
    }
}
