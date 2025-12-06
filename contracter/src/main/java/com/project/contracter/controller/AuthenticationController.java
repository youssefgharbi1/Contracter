package com.project.contracter.controller;

import com.project.contracter.model.User;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.security.jwt.JwtAuthenticationResponse;
import com.project.contracter.security.jwt.JwtService;
import com.project.contracter.security.user.CustomUserDetails;
import com.project.contracter.security.user.CustomUserDetailsService;
import com.project.contracter.security.user.UserRegistrationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Lazy
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        // Get user info from database
        User user = userDetails.getUser();

        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getJwtExpiration())
                .userInfo(JwtAuthenticationResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .build())
                .build();

        return ResponseEntity.ok(ApiResponse.<JwtAuthenticationResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        System.out.println("=== REFRESH DEBUG ===");
        System.out.println("Full request object: " + request);
        System.out.println("Refresh token received: " + (request.getRefreshToken() != null ?
                request.getRefreshToken().substring(0, Math.min(50, request.getRefreshToken().length())) + "..." : "NULL"));
        System.out.println("Token length: " + (request.getRefreshToken() != null ? request.getRefreshToken().length() : "null"));
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<JwtAuthenticationResponse>builder()
                            .success(false)
                            .message("Refresh token is required")
                            .build());
        }

        try {
            String username = jwtService.extractUsername(request.getRefreshToken());

            if (username == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<JwtAuthenticationResponse>builder()
                                .success(false)
                                .message("Invalid refresh token format")
                                .build());
            }

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<JwtAuthenticationResponse>builder()
                                .success(false)
                                .message("Invalid or expired refresh token")
                                .build());
            }

            User user = userDetails.getUser();

            var newAccessToken = jwtService.generateToken(userDetails);
            var newRefreshToken = jwtService.generateRefreshToken(userDetails);

            JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(jwtService.getJwtExpiration())
                    .userInfo(JwtAuthenticationResponse.UserInfo.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .role(user.getRole().name())
                            .build())
                    .build();

            return ResponseEntity.ok(ApiResponse.<JwtAuthenticationResponse>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<JwtAuthenticationResponse>builder()
                            .success(false)
                            .message("Invalid refresh token")
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @RequestBody UserRegistrationService.UserRegistrationRequest request) {
        try {
            User user = userRegistrationService.registerUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(response)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof CustomUserDetails customUserDetails) {
                User user = customUserDetails.getUser();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("firstName", user.getFirstName());
                userInfo.put("lastName", user.getLastName());
                userInfo.put("role", user.getRole().name());

                return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Current user retrieved successfully")
                        .data(userInfo)
                        .build());
            } else {
                return ResponseEntity.status(401)
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .success(false)
                                .message("User not authenticated")
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Authentication error: " + e.getMessage())
                            .build());
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}