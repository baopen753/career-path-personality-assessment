package org.swd392.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.ApiResponse;
import org.swd392.users.dto.UserDTO;
import org.swd392.users.dto.UserResponseDto;
import org.swd392.users.entity.User;
import org.swd392.users.mapper.UserMapper;
import org.swd392.users.service.UserService;
import org.swd392.users.service.impl.IUserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private IUserService iUserService;

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

    @GetMapping("/userid/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUser(id);
        if (userResponseDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userResponseDto);
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
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        return userService.updateUser(id, user)
                .map(updatedUser -> ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                        .code(200)
                        .message("User updated successfully")
                        .result(UserMapper.toDTO(updatedUser))
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("User deleted successfully")
                    .result("User with ID " + id + " has been deleted")
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(404)
                .message("User not found")
                .result("User with ID " + id + " not found")
                .build());
    }
    
    @GetMapping("/{userId}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable Integer userId) {
        return userService.getUserById(userId.longValue())
                .map(user -> ResponseEntity.ok(user.getEmail()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/name")
    public ResponseEntity<String> getUserName(@PathVariable Integer userId) {
        return userService.getUserById(userId.longValue())
                .map(user -> {
                    String name = user.getEmail().split("@")[0];
                    return ResponseEntity.ok(name);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<String> getUserRole(@PathVariable Integer userId) {
        return userService.getUserById(userId.longValue())
                .map(user -> ResponseEntity.ok(user.getRole().getRoleName()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/info")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable Integer userId) {
        return userService.getUserById(userId.longValue())
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmailParam(@RequestParam("email") String email) {
        try {
            UserResponseDto userResponse = userService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.<UserResponseDto>builder()
                    .code(200)
                    .message("User found successfully")
                    .result(userResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.<UserResponseDto>builder()
                    .code(404)
                    .message("User not found")
                    .result(null)
                    .build());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        UserResponseDto response = iUserService.getCurrentUser(token);
        return ResponseEntity.ok(ApiResponse.<UserResponseDto>builder()
                .code(200)
                .message("Current user information retrieved successfully")
                .result(response)
                .build());
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsersWithResponse() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<UserDTO>>builder()
                .code(200)
                .message("Users retrieved successfully")
                .result(dtos)
                .build());
    }
}