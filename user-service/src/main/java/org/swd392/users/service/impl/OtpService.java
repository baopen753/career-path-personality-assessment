package org.swd392.users.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.swd392.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailService emailService;
    private final UserRepository userRepository;

    private final Map<String, OtpData> otpStorage = new HashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5;

    public String sendOtpToEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Email is not exist");
        }

        String otp = generateOtp();
        otpStorage.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));

        String subject = "Mã OTP Xác Nhận Quên Mật Khẩu";
        String htmlMessage = "<html><body><h2>OTP: " + otp + "</h2></body></html>";

        emailService.sendMail(email, subject, htmlMessage);

        return "OTP has been sent to your email.";
    }

    public boolean verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) return false;
        OtpData data = otpStorage.get(email);
        if (data.getExpiryTime().isBefore(LocalDateTime.now())) return false;
        return data.getOtp().equals(otp);
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
