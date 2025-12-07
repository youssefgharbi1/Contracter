package com.project.contracter.security.user;

import com.project.contracter.model.User;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        // Validate new password strength
        userDetailsService.validatePasswordStrength(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPublicKey(request.getPublicKey()); // Optional for digital signatures
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    @Setter
    @Getter
    public static class UserRegistrationRequest {
        // Getters and setters
        private String username;
        @Email(message = "Please provide a valid email address")
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String publicKey; // For digital signature functionality

    }
}