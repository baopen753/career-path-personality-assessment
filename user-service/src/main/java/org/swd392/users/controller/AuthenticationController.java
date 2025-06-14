package org.swd392.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.dto.*;
import org.swd392.users.service.impl.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.<RegisterResponseDto>builder()
                .code(200)
                .message("Registration successful")
                .result(response)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.<LoginResponseDto>builder()
                .code(200)
                .message("Login successful")
                .result(response)
                .build());
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authentication") String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        userService.logout(token);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(204)
                .message("Logged out successfully")
                .build());
    }


}
