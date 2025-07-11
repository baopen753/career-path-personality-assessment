package org.swd392.quizzes.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreationRequest {
    private String id;
    private String fullName;
    private String birthDate;
    private String phone;
    private String address;
    private Integer districtCode;
    private Integer provinceCode;
}
