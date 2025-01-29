package com.example.taskline.util;

import com.example.taskline.entity.User;
import com.example.taskline.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final AuthService authService;

    private final JavaMailSender mailSender;
    private User user;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String userVerificationToken = UUID.randomUUID().toString();
        authService.saveUserVerificationToken(user, userVerificationToken);
        String userUrl = event.getApplicationUrl() + "/auth/verifyUserEmail?token=" + userVerificationToken;
        try {
            sendPatientVerificationEmail(userUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPatientVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify your email address for TaskLine";
        String senderName = "TaskLine Team";
        String mailContent = "<p> Hi " + user.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us." + " " +
                "Please follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p>Thank you,<br>TaskLine Support Team";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("taskline.team@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}