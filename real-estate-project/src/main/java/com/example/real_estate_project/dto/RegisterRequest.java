package com.example.real_estate_project.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
}
