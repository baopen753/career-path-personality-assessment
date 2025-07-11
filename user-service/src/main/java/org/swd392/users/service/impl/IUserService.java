package org.swd392.users.service.impl;

import org.springframework.stereotype.Service;
import org.swd392.users.dto.*;
import org.swd392.users.entity.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> getUserById(Long userId);
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String token);

    // Add missing methods for quiz-service integration
    TokenValidationResponseDto introspectToken(String token);
    UserResponseDto getCurrentUser(String token);
    UserResponseDto getUserByEmail(String email);
}
