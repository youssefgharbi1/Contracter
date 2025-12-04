package com.project.contracter.service;


import com.project.contracter.model.*;
import com.project.contracter.repository.*;
import com.project.contracter.service.serviceInterface.*;
import com.project.contracter.dtos.user.UserCreateDTO;
import com.project.contracter.dtos.user.UserDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserServiceI {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO dto) throws ConflictException {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        // Password must be hashed by the caller/service layer; here we store as-is placeholder
        u.setPasswordHash(dto.getPassword());
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());
        User saved = userRepository.save(u);
        return mapToDTO(saved);
    }

    @Override
    public UserDTO getById(Long id) throws ResourceNotFoundException {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(u);
    }

    @Override
    public Optional<User> findEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByEmailOrUsername(usernameOrEmail, usernameOrEmail);
    }
    @Override
    public List<UserDTO> searchUsernames(String username){
        return userRepository.findByUsernameContainingIgnoreCase(username).stream().map(this::mapToDTO).toList();
    }

    @Override
    public UserDTO findByEmail(String email) throws ResourceNotFoundException {
        User u = userRepository.findByUsername(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(u);
    }

    @Override
    public UserDTO findByUsername(String username) throws ResourceNotFoundException {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(u);
    }
    @Override
    public UserDTO updateUser(Long id, UserCreateDTO request) {
        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        // Update fields
        existingUser.setEmail(request.getEmail());
        existingUser.setUsername(request.getUsername());
        existingUser.setLastName(request.getLastName());
        existingUser.setFirstName(request.getFirstName());

        // Save updated user
        User updatedUser = userRepository.save(existingUser);

        // Map to DTO
        return mapToDTO(updatedUser); // assuming you have a mapper or manual mapping
    }

    @Override
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));


        userRepository.delete(existingUser);
    }


    private UserDTO mapToDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}

