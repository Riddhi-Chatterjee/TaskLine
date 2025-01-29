package com.example.taskline.util;

import com.example.taskline.entity.Alert;
import com.example.taskline.entity.Task;
import com.example.taskline.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TaskAlertEvent extends ApplicationEvent {
    private User user;
    private Task task;
    private Alert alert;

    public TaskAlertEvent(User user, Task task, Alert alert) {
        super(user);
        this.user = user;
        this.task = task;
        this.alert = alert;
    }
}


