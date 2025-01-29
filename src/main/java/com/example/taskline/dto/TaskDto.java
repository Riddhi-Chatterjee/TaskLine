package com.example.taskline.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskDto {
    private String description;
    private LocalDateTime deadline;
    private List<LocalDateTime> alertTimes;
}
