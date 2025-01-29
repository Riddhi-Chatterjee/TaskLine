package com.example.taskline.repository;

import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.isCompleted = false")
    List<Task> findByUserAndCompletedFalse(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.isCompleted = true")
    List<Task> findByUserAndCompletedTrue(@Param("user") User user);

    Optional<Task> findByIdAndUser(Long id, User user);
}
