package com.gideon.knowmate.Service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailVerification(String email, String code) throws MessagingException;
    void sendWelcomeEmail();
    void sendPasswordReset();
}
