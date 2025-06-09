package org.swd392.users.mapper;

import org.swd392.users.dto.UserDTO;
import org.swd392.users.entity.User;

public class UserMapper {

    // Chuyển từ User entity sang UserDTO
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isStatus()
        );
    }

    // Chuyển từ UserDTO sang User entity
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setStatus(dto.isStatus());
        return user;
    }
}
