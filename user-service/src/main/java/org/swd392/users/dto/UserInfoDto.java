package org.swd392.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
    private Long userId;

    private String fullName;

    private String email;
    
    private String role;
}
