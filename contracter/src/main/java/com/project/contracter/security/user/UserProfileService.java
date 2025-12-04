package com.project.contracter.security.user;

import com.project.contracter.model.User;
import com.project.contracter.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

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
        // You might want to verify old password here
        // This would require injecting PasswordEncoder
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Setter
    @Getter
    public static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String publicKey;


    }

    @Setter
    @Getter
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

    }
}