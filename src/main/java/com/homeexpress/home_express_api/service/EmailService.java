package com.homeexpress.home_express_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from:${spring.mail.username}}")
    private String fromEmail;

    // Gui email OTP
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Home Express - Mã xác thực OTP");
            message.setText(
                    "Chào mừng bạn đến với HomeExpress,\n\n"
                    + "Mã OTP của bạn là: " + otpCode + "\n\n"
                    + "Mã này có hiệu lực trong 5 phút.\n\n"
                    + "Nếu bạn không yêu cầu tạo tài khoản hoặc reset mật khẩu, vui lòng bỏ qua email này.\n\n"
                    + "Trân trọng,\n"
                    + "Home Express Team"
            );

            mailSender.send(message);
        } catch (Exception e) {
            // Log the full stack trace for debugging
            System.err.println("FAILED TO SEND EMAIL: " + e.getMessage());
            if (e instanceof MailAuthenticationException) {
                System.err.println("Possible Cause: Invalid Gmail credentials. Check your App Password and ensure 'Less secure app access' is handled or 2FA is on.");
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
