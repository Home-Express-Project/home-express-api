package com.homeexpress.home_express_api.service;

import com.homeexpress.home_express_api.entity.OtpCode;
import com.homeexpress.home_express_api.repository.OtpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class OtpService {

    private final OtpRepository otpRepository;

    private final EmailService emailService;

    // Generate OTP 6 chu so
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Tao va gui OTP qua email
    public void createAndSendOtp(String email) {
        // Generate OTP code
        String otpCode = generateOtp();

        // Thoi gian het han: 5 phut
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // Luu OTP vao database
        OtpCode otp = new OtpCode(email, otpCode, expiresAt);
        otpRepository.save(otp);

        // Gui email
        try {
            emailService.sendOtpEmail(email, otpCode);
        } catch (Exception e) {
            // Log warning but allow transaction to commit so user is registered
            // User can request OTP resend later if email config is fixed
            System.err.println("WARNING: Failed to send OTP email to " + email + ". User created but email not sent. Error: " + e.getMessage());
        }
    }

    // Verify OTP (Consumes it)
    public boolean verifyOtp(String email, String code) {
        // Tim OTP chua su dung
        OtpCode otp = otpRepository.findByEmailAndCodeAndIsUsedFalse(email, code)
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));

        // Kiem tra het han chua
        if (otp.isExpired()) {
            throw new RuntimeException("OTP code has expired");
        }

        // Danh dau da su dung
        otp.setIsUsed(true);
        otpRepository.save(otp);

        return true;
    }

    // Validate OTP without consuming (for UI check)
    public boolean validateOtp(String email, String code) {
        OtpCode otp = otpRepository.findByEmailAndCodeAndIsUsedFalse(email, code)
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));

        if (otp.isExpired()) {
            throw new RuntimeException("OTP code has expired");
        }
        
        return true;
    }

    // Xoa OTP het han (chay dinh ky)
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}

