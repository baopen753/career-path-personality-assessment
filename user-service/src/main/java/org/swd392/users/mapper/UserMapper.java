package org.swd392.users.mapper;

import org.swd392.users.dto.UserDto;
import org.swd392.users.entity.User;

public class UserMapper {

    // Chuyển từ User entity sang UserDto
    public static UserDto toDTO(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().getRoleId(),
                user.isStatus()
        );
    }

    // Chuyển từ UserDto sang User entity
    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.getRole().setRoleId(dto.getRoleId());
        user.setStatus(dto.isStatus());
        return user;
    }
}
