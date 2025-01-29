package com.example.taskline.service;

import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import com.example.taskline.repository.AlertRepository;
import com.example.taskline.util.RegistrationCompleteEvent;
import com.example.taskline.util.TaskAlertEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final ApplicationEventPublisher publisher;

    @Scheduled(fixedRate = 1000) // Runs every second
    public void checkAndSendAlerts() {
        LocalDateTime now = LocalDateTime.now();

        // Fetch alerts that are due and not yet notified
        List<Alert> alertsToNotify = alertRepository.findAllByAlertTimeBeforeAndNotifiedFalse(now);

        for (Alert alert : alertsToNotify) {
            sendAlert(alert);
        }
    }

    private void sendAlert(Alert alert) {
        Task task = alert.getTask();
        User user = task.getUser();
        publisher.publishEvent(new TaskAlertEvent(user, task, alert));

        // Mark alert as notified
        alert.setNotified(true);
        alertRepository.save(alert);
    }
}

