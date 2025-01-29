package com.example.taskline.controller;

import com.example.taskline.dto.LoginRequestDto;
import com.example.taskline.dto.UserRegistrationDto;
import com.example.taskline.entity.User;
import com.example.taskline.entity.UserVerificationToken;
import com.example.taskline.repository.UserVerificationTokenRepository;
import com.example.taskline.security.jwt.JWTService;
import com.example.taskline.service.AuthService;
import com.example.taskline.util.RegistrationCompleteEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher publisher;
    private final UserVerificationTokenRepository userVerificationTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto, final HttpServletRequest request) {
        User user = authService.registerUser(userDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return ResponseEntity.ok("Thanks for registering in TaskLine! Check your email for verification.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        String username = loginDto.getUsername();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginDto.getPassword()));
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(username));
        } else {
            throw new RuntimeException("Invalid user credentials");
        }
    }

    @GetMapping("/verifyUserEmail")
    public ResponseEntity<String> verifyUserEmail(@RequestParam("token") String token) {
        Optional<UserVerificationToken> userVerificationTokenOptional = userVerificationTokenRepository.findByToken(token);
        if(userVerificationTokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user verification token.");
        }

        UserVerificationToken userVerificationToken = userVerificationTokenOptional.get();
        if (userVerificationToken.getUser().isVerified()) {
            return ResponseEntity.ok("This account has already been verified. Please login!");
        }
        String verificationResult = authService.validateUserVerificationToken(userVerificationToken);
        if (verificationResult.equals("valid")) {
            return ResponseEntity.ok("Email verified successfully! You can now login to your account.");
        }
        return ResponseEntity.badRequest().body("Invalid user verification token.");
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}

