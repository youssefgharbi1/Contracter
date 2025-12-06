package com.project.contracter.security.user;

import com.project.contracter.model.User;
import com.project.contracter.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPublicKey() != null) {
            user.setPublicKey(request.getPublicKey());
        }

        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        // Verify old password matches
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Verify new password is different from old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new RuntimeException("New password must be different from old password");
        }

        // Validate new password strength (optional)
        validatePasswordStrength(request.getNewPassword());

        // Encode and set new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(java.time.Instant.now());

        userRepository.save(user);
    }

    // Optional: Password strength validation
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("Password must contain at least one lowercase letter");
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("Password must contain at least one digit");
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new RuntimeException("Password must contain at least one special character");
        }
    }


    // Inner classes for request DTOs
    @Getter
    @Setter
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;

        // Validation method
        public void validate() {
            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                throw new RuntimeException("New password and confirmation do not match");
            }
        }
    }


    @Setter
    @Getter
    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String publicKey;


    }
}