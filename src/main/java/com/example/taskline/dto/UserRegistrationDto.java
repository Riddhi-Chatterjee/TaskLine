package com.example.taskline.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
}

