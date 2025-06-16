package org.swd392.users.dto;


import org.swd392.users.entity.Role;

public class UserDTO {

    private String email;
    private String password; // Optional, if you want to include password in DTO
    private Role role;
    private boolean status;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(String email, String password, Role role, boolean status) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
    

