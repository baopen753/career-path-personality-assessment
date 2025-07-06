package org.swd392.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.swd392.users.dto.ResetPasswordDTO;
import org.swd392.users.dto.ResponseDTO;
import org.swd392.users.service.UserService;
import org.swd392.users.service.impl.OtpService;


@Slf4j
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<ResponseDTO<String>> sendOtp(@RequestParam String email) {
        try {
            String result = otpService.sendOtpToEmail(email);
            return ResponseEntity.ok(
                    ResponseDTO.<String>builder()
                            .status(200)
                            .message(result)
                            .build()
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.<String>builder()
                            .status(404)
                            .message("Email is not valid or does not exist.")
                            .build());
        } catch (Exception e) {
            log.error("❌ Error when sending OTP email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.<String>builder()
                            .status(500)
                            .message("Something went wrong, please try again!")
                            .build());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO<String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(
                    ResponseDTO.<String>builder()
                            .status(200)
                            .message("OTP is valid. You can reset your password.")
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                ResponseDTO.<String>builder()
                        .status(400)
                        .message("OTP is invalid or expired.")
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(
            @RequestParam String email,
            @Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
            org.springframework.validation.BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("\n"));

            return ResponseEntity.badRequest().body(
                    ResponseDTO.<String>builder()
                            .status(400)
                            .message(errorMessage.toString())
                            .build()
            );
        }

        userService.updatePassword(resetPasswordDTO, email);
        return ResponseEntity.ok(
                ResponseDTO.<String>builder()
                        .status(200)
                        .message("Password reset successful.")
                        .build()
        );
    }
}