package org.swd392.users.dto;


public class UserDTO {

    private String email;
    private String password; // Optional, if you want to include password in DTO
    private RoleDto roleDto;
    private boolean status;

    // Constructors
    public UserDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleDto getRoleDto() {
        return roleDto;
    }

    public void setRoleDto(RoleDto roleDto) {
        this.roleDto = roleDto;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
    

