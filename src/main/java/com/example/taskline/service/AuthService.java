package com.example.taskline.service;

import com.example.taskline.dto.UserRegistrationDto;
import com.example.taskline.entity.User;
import com.example.taskline.entity.UserVerificationToken;
import com.example.taskline.repository.UserRepository;
import com.example.taskline.repository.UserVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserVerificationTokenRepository userVerificationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstname());
        user.setLastName(userDto.getLastname());
        user.setUsername(userDto.getUsername());
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);
        user.setEmail(userDto.getEmail());
        user.setVerified(false);

        userRepository.save(user);
        return user;
    }

    public void saveUserVerificationToken(User user, String token) {
        UserVerificationToken userVerificationToken = new UserVerificationToken(token, user);
        userVerificationTokenRepository.save(userVerificationToken);
    }

    @Transactional
    public String validateUserVerificationToken(UserVerificationToken userVerificationToken) {
        User user = userVerificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((userVerificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            userRepository.delete(user);
            return "Token already expired";
        }
        user.setVerified(true);
        userRepository.save(user);
        userVerificationTokenRepository.delete(userVerificationToken);
        return "valid";
    }
}

