package com.project.contracter.dtos.user;

import java.time.Instant;

// For returning user info (public view)
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Instant createdAt;
}

