package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String id;
    private String fullName;
    private String birthDate;
    private String avatarUrl;
    private String phone;
    private String address;
    private String district;
    private String province;
    private String personalityType;
    private String personalityDescription;
}
