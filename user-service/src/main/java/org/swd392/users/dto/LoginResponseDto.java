package org.swd392.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private Long userId;
    private String email;
    private Integer roleId;
    private String roleName;
    private String message;
    private boolean success;
    private String token; // For future JWT implementation
}
