package org.swd392.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.swd392.users.dto.LoginRequestDto;
import org.swd392.users.dto.LoginResponseDto;
import org.swd392.users.dto.RegisterRequestDto;
import org.swd392.users.dto.RegisterResponseDto;
import org.swd392.users.entity.Role;
import org.swd392.users.entity.User;
import org.swd392.users.repository.RoleRepository;
import org.swd392.users.repository.UserRepository;
import org.swd392.users.service.impl.IUserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setStatus(updatedUser.isStatus());
            return userRepository.save(existingUser);
        });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }



    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Role defaultRole = roleRepository.findById(3)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default user role not found"));

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(defaultRole);

        User savedUser = userRepository.save(newUser);

        return RegisterResponseDto.builder()
                .useId(savedUser.getId())
                .email(savedUser.getEmail())
                .roleId(savedUser.getRole().getRoleId())
                .build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .roleId(user.getRole().getRoleId())
                .roleName(user.getRole().getRoleName())
                .success(true)
                .message("Login successful")
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null) {
            jwtService.invalidateToken(token);
        }
    }
}
