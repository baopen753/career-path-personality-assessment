package org.swd392.users.mapper;

import org.swd392.users.dto.RoleDto;
import org.swd392.users.entity.Role;

public class RoleMapper {
    public static RoleDto mapToDto(Role role) {
        if (role == null) return null;
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleId(role.getRoleId());
        roleDto.setRoleName(role.getRoleName());
        return roleDto;
    }

    public static Role mapToEntity(RoleDto roleDto) {
        if (roleDto == null) return null;
        Role role = new Role();
        role.setRoleId(roleDto.getRoleId());
        role.setRoleName(roleDto.getRoleName());
        return role;
    }
}