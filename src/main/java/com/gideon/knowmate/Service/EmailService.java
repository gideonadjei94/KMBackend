package com.gideon.knowmate.Service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailVerification(String email, String code) throws MessagingException;
    void sendWelcomeEmail(String email, String username) throws MessagingException;
    void sendPasswordReset(String email, String OTP) throws MessagingException;
}
