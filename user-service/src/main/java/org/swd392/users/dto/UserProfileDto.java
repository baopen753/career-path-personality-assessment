package org.swd392.users.dto;

import lombok.*;
import org.swd392.users.entity.AccountType;
import org.swd392.users.entity.Gender;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserProfileDto {
    private Long userId;
    private Long profileId;
    private String fullName;
    private LocalDate birthDay;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String school;
    private AccountType accountType;
    private Gender gender;


}
