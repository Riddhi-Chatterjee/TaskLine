package com.example.taskline.service;

import com.example.taskline.dto.TaskDto;
import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import com.example.taskline.repository.AlertRepository;
import com.example.taskline.repository.TaskRepository;
import com.example.taskline.util.TaskUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AlertRepository alertRepository;
    private final TaskUtils taskUtils;

    @Transactional
    public void createTask(TaskDto taskDto, User user) {
        Task task = new Task();
        task.setDescription(taskDto.getDescription());
        task.setDeadline(taskDto.getDeadline());
        task.setUser(user);
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        if (taskDto.getAlertTimes() != null && !taskDto.getAlertTimes().isEmpty()) {
            List<Alert> alerts = taskDto.getAlertTimes().stream()
                    .map(alertTime -> {
                        Alert alert = new Alert();
                        alert.setAlertTime(alertTime);
                        alert.setTask(savedTask);
                        return alert;
                    }).collect(Collectors.toList());

            alertRepository.saveAll(alerts);
        }
    }

    public void addAlertsToTask(Long taskId, User user, List<LocalDateTime> alertTimes){
        Task task = validateTaskOwnership(taskId, user);
        if (!taskUtils.areAlertTimesValid(alertTimes, task.getDeadline())) {
            throw new RuntimeException("Invalid alert times");
        }
        List<Alert> alerts = alertTimes.stream().map(alertTime -> {
            Alert alert = new Alert();
            alert.setAlertTime(alertTime);
            alert.setTask(task);
            return alert;
        }).collect(Collectors.toList());
        alertRepository.saveAll(alerts); // Add the new alerts for this task
    }

    @Transactional
    public void updateTask(Long taskId, TaskDto taskDto, User user) {
        Task task = validateTaskOwnership(taskId, user);
        if (taskDto.getDescription() != null) task.setDescription(taskDto.getDescription());
        if (taskDto.getDeadline() != null)
        {
            if (!taskUtils.isDeadlineValid(taskDto.getDeadline())) {
                throw new RuntimeException("Invalid deadline");
            }
            task.setDeadline(taskDto.getDeadline());
        }
        if (taskDto.getAlertTimes() != null && !taskDto.getAlertTimes().isEmpty()) {
            if (!taskUtils.areAlertTimesValid(taskDto.getAlertTimes(), task.getDeadline()))
            {
                throw new RuntimeException("Invalid alert times");
            }
            List<Alert> alerts = taskDto.getAlertTimes().stream().map(alertTime -> {
                Alert alert = new Alert();
                alert.setAlertTime(alertTime);
                alert.setTask(task);
                return alert;
            }).collect(Collectors.toList());
            alertRepository.deleteAllByTask(task); // Delete existing alerts for this task
            alertRepository.saveAll(alerts); // Set the new alerts for this task
        }
        if (taskDto.getAlertTimes() != null && taskDto.getAlertTimes().isEmpty())
        {
            alertRepository.deleteAllByTask(task); // Delete existing alerts for this task
        }
        taskRepository.save(task);
    }

    public void markTaskAsCompleted(Long taskId, User user) {
        Task task = validateTaskOwnership(taskId, user);
        task.setCompleted(true);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId, User user) {
        Task task = validateTaskOwnership(taskId, user);
        alertRepository.deleteAllByTask(task);
        taskRepository.delete(task);
    }

    @Transactional
    public void deleteAllCompletedTasks(User user) {
        List<Task> completedTasks = taskRepository.findByUserAndCompletedTrue(user);

        for (Task task : completedTasks) {
            alertRepository.deleteAllByTask(task);
        }

        taskRepository.deleteAll(completedTasks);
    }

    public List<Task> getUserTasks(User user) {
        return taskRepository.findByUser(user);
    }

    public List<Task> getPendingTasks(User user) {
        return taskRepository.findByUserAndCompletedFalse(user);
    }

    public List<Task> getCompletedTasks(User user) {
        return taskRepository.findByUserAndCompletedTrue(user);
    }

    public Task getTaskById(Long taskId, User user) {
        return validateTaskOwnership(taskId, user);
    }

    public List<Alert> getAlertsForTask(Long taskId, User user) {
        Task task = validateTaskOwnership(taskId, user);
        return alertRepository.findByTask(task);
    }

    private Task validateTaskOwnership(Long taskId, User user) {
        return taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found for user"));
    }
}
