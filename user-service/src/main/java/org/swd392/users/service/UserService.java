package org.swd392.users.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.swd392.users.dto.*;
import org.swd392.users.entity.Role;
import org.swd392.users.entity.User;
import org.swd392.users.event.UserRegisteredEvent;
import org.swd392.users.event.producer.EventProducer;
import org.swd392.users.entity.UserProfile;
import org.swd392.users.repository.RoleRepository;
import org.swd392.users.repository.UserRepository;
import org.swd392.users.service.impl.IUserService;
import org.swd392.users.service.client.NotificationFeignClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EventProducer<UserRegisteredEvent> eventProducer;
    private final String LOGIN_URL = "http://localhost:8072/swd391/user/authentication/login";

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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


    public void updatePassword(@Valid ResetPasswordDTO resetPasswordDTO, String email) {
        {
            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
            }

            user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    //hàm cho quiz service gọi để lấy thông tin user
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .noPassword(user.getPassword() == null || user.getPassword().isEmpty())
                .role(user.getRole().getRoleName())
                .build();
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Role defaultRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default user role not found"));

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(true);
        newUser.setRole(defaultRole);
        //tạo 1 profile mới empty cho user khi register
        UserProfile newProfile = new UserProfile();
        newProfile.setUser(newUser);
        newUser.setUserProfile(newProfile);

        User savedUser = userRepository.save(newUser);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .email(newUser.getEmail())
                .accountType(newUser.getRole().getRoleName())
                .registrationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .loginLink(LOGIN_URL)
                .build();

        // send confirmation email after registration
        eventProducer.sendMessage(event);

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

    // New methods for quiz-service integration
    @Override
    public UserResponseDto getCurrentUser(String token) {
        try {
            if (!jwtService.isValidToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }

            Long userId = jwtService.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            return UserResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .noPassword(user.getPassword() == null || user.getPassword().isEmpty())
                    .role(user.getRole().getRoleName())
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to get current user: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .noPassword(user.getPassword() == null || user.getPassword().isEmpty())
                .role(user.getRole().getRoleName())
                .build();
    }
}
