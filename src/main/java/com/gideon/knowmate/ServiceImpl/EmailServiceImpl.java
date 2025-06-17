package com.gideon.knowmate.ServiceImpl;

import com.gideon.knowmate.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Override
    public void sendEmailVerification(String email, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Email Verification");

        Context context = new Context();
        context.setVariable("OTP", code);
        String htmlContent = templateEngine.process("EmailVerification", context);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }


    @Override
    public void sendWelcomeEmail(String email, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Welcome!");

        Context context = new Context();
        context.setVariable("username", username);
        String htmlContent = templateEngine.process("Welcome", context);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }


    @Override
    public void sendPasswordReset(String email, String OTP) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Reset Password");

        Context context = new Context();
        context.setVariable("OTP", OTP);
        String htmlContent = templateEngine.process("ResetPassword", context);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
