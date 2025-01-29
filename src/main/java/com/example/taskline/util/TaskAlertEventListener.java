package com.example.taskline.util;

import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class TaskAlertEventListener implements ApplicationListener<TaskAlertEvent> {

    private final JavaMailSender mailSender;
    private User user;
    private Task task;
    private Alert alert;

    @Override
    public void onApplicationEvent(TaskAlertEvent event) {
        user = event.getUser();
        task = event.getTask();
        alert = event.getAlert();
        try {
            sendAlertEmail();
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAlertEmail() throws MessagingException, UnsupportedEncodingException {
        String subject = "Task Reminder from TaskLine";
        String senderName = "TaskLine Team";
        String mailContent = "<p>Hi " + user.getFirstName() + ",</p>" +
                "<p>This is a reminder for your task:</p>" +
                "<p><b>Task Description:</b> " + task.getDescription() + "</p>" +
                "<p><b>Deadline:</b> " + task.getDeadline() + "</p>" +
                "<p>Stay on top of your tasks with TaskLine!</p>" +
                "<p>Thank you,<br>TaskLine Support Team</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom("taskline.team@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);

        mailSender.send(message);
    }
}