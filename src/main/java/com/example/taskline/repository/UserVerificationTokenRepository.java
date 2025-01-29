package com.example.taskline.repository;

import com.example.taskline.entity.User;
import com.example.taskline.entity.UserVerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface UserVerificationTokenRepository extends JpaRepository<UserVerificationToken, Long> {
    Optional<UserVerificationToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteAllByUser(User user);
}