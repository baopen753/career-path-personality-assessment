package org.swd392.users.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swd392.users.entity.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password; // Optional, if you want to include password in DTO
    private Integer roleId;
    private boolean status;

}

