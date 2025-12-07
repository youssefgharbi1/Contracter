package com.project.contracter.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    private String username;

    @Email(message = "Please provide a valid email address")
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String firstName;
    private String lastName;
}
