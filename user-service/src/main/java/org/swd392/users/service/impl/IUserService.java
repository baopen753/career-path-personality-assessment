package org.swd392.users.service.impl;

import org.springframework.stereotype.Service;
import org.swd392.users.dto.LoginRequestDto;
import org.swd392.users.dto.LoginResponseDto;
import org.swd392.users.dto.RegisterRequestDto;
import org.swd392.users.dto.RegisterResponseDto;
import org.swd392.users.entity.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> getUserById(Long userId);
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String token);
}
