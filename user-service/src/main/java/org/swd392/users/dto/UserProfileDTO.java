package org.swd392.users.dto;

import lombok.*;
import org.swd392.users.entity.Gender;
import org.swd392.users.entity.UserProfile;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserProfileDTO {
    private Long userId;
    private Long profileId;
    private String fullName;
    private LocalDate birthDay;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String school;
    private UserProfile.AccountType accountType;
    private Gender gender;


}
