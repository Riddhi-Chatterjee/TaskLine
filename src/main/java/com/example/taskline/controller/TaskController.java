package com.example.taskline.controller;

import com.example.taskline.dto.TaskDto;
import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import com.example.taskline.service.TaskService;
import com.example.taskline.service.UserService;
import com.example.taskline.util.TaskUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskUtils taskUtils;

    private User validateUser(String username, UserDetails userDetails) {
        String headerUsername = userDetails.getUsername();
        if (!headerUsername.equals(username)) {
            throw new RuntimeException("Unauthorized access to user tasks");
        }

        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @PostMapping("/add/{username}")
    public ResponseEntity<String> addTask(@PathVariable String username,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody TaskDto taskDto) {
        User user = validateUser(username, userDetails);
        if (taskDto.getDescription() == null)
        {
            throw new RuntimeException("Task description is mandatory");
        }
        if (taskDto.getDeadline() == null)
        {
            throw new RuntimeException("Task deadline is mandatory");
        }
        if (!taskUtils.isDeadlineValid(taskDto.getDeadline())) {
            throw new RuntimeException("Invalid deadline");
        }
        if (!(taskDto.getAlertTimes() == null || taskDto.getAlertTimes().isEmpty()) && !taskUtils.areAlertTimesValid(taskDto.getAlertTimes(), taskDto.getDeadline()))
        {
            throw new RuntimeException("Invalid alert times");
        }
        taskService.createTask(taskDto, user);
        return ResponseEntity.ok("Task added successfully");
    }

    @PostMapping("/addAlerts/{taskId}/{username}")
    public ResponseEntity<String> addAlertsToTask(@PathVariable Long taskId,
                                                  @PathVariable String username,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody List<LocalDateTime> alertTimes) {
        User user = validateUser(username, userDetails);
        if (alertTimes != null && !alertTimes.isEmpty()) {
            taskService.addAlertsToTask(taskId, user, alertTimes);
        }
        return ResponseEntity.ok("Alerts added to task successfully");
    }

    @PutMapping("/modify/{taskId}/{username}")
    public ResponseEntity<String> modifyTask(@PathVariable Long taskId,
                                           @PathVariable String username,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody TaskDto taskDto) {
        User user = validateUser(username, userDetails);
        taskService.updateTask(taskId, taskDto, user);
        return ResponseEntity.ok("Task modified successfully");
    }

    @PutMapping("/complete/{taskId}/{username}")
    public ResponseEntity<String> markTaskAsCompleted(@PathVariable Long taskId,
                                                      @PathVariable String username,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        taskService.markTaskAsCompleted(taskId, user);
        return ResponseEntity.ok("Task marked as completed");
    }

    @DeleteMapping("/delete/{taskId}/{username}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId,
                                             @PathVariable String username,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        taskService.deleteTask(taskId, user);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @DeleteMapping("/delete/completed/{username}")
    public ResponseEntity<String> deleteCompletedTasks(@PathVariable String username,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        taskService.deleteAllCompletedTasks(user);
        return ResponseEntity.ok("All completed tasks deleted successfully");
    }

    @GetMapping("/all/{username}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable String username,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        List<Task> tasks = taskService.getUserTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/pending/{username}")
    public ResponseEntity<List<Task>> getPendingTasks(@PathVariable String username,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        List<Task> tasks = taskService.getPendingTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/completed/{username}")
    public ResponseEntity<List<Task>> getCompletedTasks(@PathVariable String username,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        List<Task> tasks = taskService.getCompletedTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}/{username}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId,
                                            @PathVariable String username,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        Task task = taskService.getTaskById(taskId, user);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/alerts/{taskId}/{username}")
    public ResponseEntity<List<Alert>> getAlertsForTask(@PathVariable Long taskId,
                                                        @PathVariable String username,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(username, userDetails);
        List<Alert> alerts = taskService.getAlertsForTask(taskId, user);
        return ResponseEntity.ok(alerts);
    }
}
