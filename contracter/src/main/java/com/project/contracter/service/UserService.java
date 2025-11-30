package com.project.contracter.service;


import com.project.contracter.dtos.contractAttachment.ContractAttachmentCreateDTO;
import com.project.contracter.dtos.contractAttachment.ContractAttachmentDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.dtos.contractParticipant.ContractParticipantCreateDTO;
import com.project.contracter.dtos.contractParticipant.ContractParticipantDTO;
import com.project.contracter.dtos.contractTemplate.ContractTemplateCreateDTO;
import com.project.contracter.dtos.contractTemplate.ContractTemplateDTO;
import com.project.contracter.dtos.signature.SignatureCreateDTO;
import com.project.contracter.dtos.signature.SignatureDTO;
import com.project.contracter.enums.ContractStatus;
import com.project.contracter.enums.ParticipantRole;
import com.project.contracter.exception.UnauthorizedException;
import com.project.contracter.model.*;
import com.project.contracter.repository.*;
import com.project.contracter.service.serviceInterface.*;
import com.project.contracter.dtos.user.UserCreateDTO;
import com.project.contracter.dtos.user.UserDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceI {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

