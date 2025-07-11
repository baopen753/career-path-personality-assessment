package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String birthDate;
    private String address;
    private Boolean isParent;
}
