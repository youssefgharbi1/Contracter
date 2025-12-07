package com.project.contracter.dtos.user;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    @Email(message = "Please provide a valid email address")
    private String email;
    private String firstName;
    private String lastName;
    private Instant createdAt;
}

