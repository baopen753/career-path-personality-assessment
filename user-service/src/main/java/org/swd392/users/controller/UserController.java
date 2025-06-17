package org.swd392.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.UserDTO;
import org.swd392.users.entity.User;
import org.swd392.users.mapper.UserMapper;
import org.swd392.users.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #id == authentication.principal.id))")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }


    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #id == authentication.principal.id))")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        return userService.updateUser(id, user)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}