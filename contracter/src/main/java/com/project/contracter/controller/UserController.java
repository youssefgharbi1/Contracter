package com.project.contracter.controller;

import com.project.contracter.dtos.user.UserDTO;
import com.project.contracter.dtos.user.UserCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.service.serviceInterface.UserServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceI userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserCreateDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User created successfully")
                .data(createdUser)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User found successfully")
                .data(user)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@RequestParam String value) {
        UserDTO user = userService.findByEmail(value);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User found successfully")
                .data(user)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User Updated successfully")
                .data(updatedUser)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User Deleted successfully")
                .build());
    }
}
