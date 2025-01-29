package com.example.taskline.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime alertTime; // Time for the alert
    private boolean notified; // Whether the alert has been sent

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task; // Associated task

}

