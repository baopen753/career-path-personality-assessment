package org.swp392.users.dto;

import org.swp392.users.entity.Role;

public class UserDTO {
    private Long id;
    private String email;
    private String password; // Optional, if you want to include password in DTO
    private Role role;
    private boolean status;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(Long id, String email,String password ,Role role, boolean status) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

