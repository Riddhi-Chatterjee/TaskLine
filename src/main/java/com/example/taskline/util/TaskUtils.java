package com.example.taskline.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskUtils {
    public boolean isDeadlineValid(LocalDateTime deadline) {
        return deadline.isAfter(LocalDateTime.now());
    }

    public boolean areAlertTimesValid(List<LocalDateTime> alertTimes, LocalDateTime deadline) {
        return alertTimes.stream().allMatch(alert -> (alert.isBefore(deadline) || alert.isEqual(deadline)) && alert.isAfter(LocalDateTime.now()));
    }
}

