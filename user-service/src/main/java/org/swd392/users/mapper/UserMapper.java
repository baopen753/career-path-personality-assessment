package org.swd392.users.mapper;

import org.swd392.users.dto.UserDTO;
import org.swd392.users.entity.User;

public class UserMapper {


    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRoleDto(RoleMapper.mapToDto(user.getRole()));
        dto.setStatus(user.isStatus());
        return dto;
    }


    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(RoleMapper.mapToEntity(dto.getRoleDto()));
        user.setStatus(dto.isStatus());
        return user;
    }
}
