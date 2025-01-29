package com.example.taskline.controller;

import com.example.taskline.entity.User;
import com.example.taskline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUserAccount(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        userService.deleteUserAccount(username);
        return ResponseEntity.ok("User account deleted successfully.");
    }
}
