package org.swd392.users.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserInfoDto {
    private Long userId;
    private String fullName;
    private String email;
}
