package com.homeexpress.home_express_api.controller;

import com.homeexpress.home_express_api.dto.request.*;
import com.homeexpress.home_express_api.dto.response.AuthResponse;
import com.homeexpress.home_express_api.service.AuthService;
import com.homeexpress.home_express_api.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final OtpService otpService;

    // Endpoint: Dang ky user moi
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint: Dang nhap
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Endpoint: Verify Registration OTP and Login
    @PostMapping("/verify-registration")
    public ResponseEntity<AuthResponse> verifyRegistration(@RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyRegistration(request.getEmail(), request.getCode());
        return ResponseEntity.ok(response);
    }

    // Endpoint: Resend Verification OTP
    @PostMapping("/resend-verification-otp")
    public ResponseEntity<?> resendVerificationOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        otpService.createAndSendOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
    }

    // Endpoint: Yeu cau OTP reset password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        otpService.createAndSendOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
    }

    // Endpoint: Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of(
                "message", "OTP verified successfully",
                "verified", String.valueOf(isValid)
        ));
    }

    // Endpoint: Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Verify and consume OTP
        otpService.verifyOtp(request.getEmail(), request.getOtpCode());
        
        authService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth API is working!");
    }
}

