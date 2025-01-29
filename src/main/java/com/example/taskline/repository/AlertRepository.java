package com.example.taskline.repository;

import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE a.alertTime < :now AND a.notified = false")
    List<Alert> findAllByAlertTimeBeforeAndNotifiedFalse(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    void deleteAllByTask(Task task);

    List<Alert> findByTask(Task task);
}
