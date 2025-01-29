package com.example.taskline.service;

import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import com.example.taskline.repository.TaskRepository;
import com.example.taskline.repository.UserRepository;
import com.example.taskline.repository.UserVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final UserVerificationTokenRepository userVerificationTokenRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User profile not found for username: " + username));
    }

    @Transactional
    public void deleteUserAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Task> userTasks = taskRepository.findByUser(user);
        userTasks.forEach(task -> taskService.deleteTask(task.getId(), user));

        userVerificationTokenRepository.deleteAllByUser(user);

        userRepository.delete(user);
    }
}
