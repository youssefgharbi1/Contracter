package com.project.contracter.service;

import com.project.contracter.dtos.contractTemplate.ContractTemplateCreateDTO;
import com.project.contracter.dtos.contractTemplate.ContractTemplateDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.ContractTemplate;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractTemplateRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.ContractTemplateServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractTemplateService implements ContractTemplateServiceI {

    private final ContractTemplateRepository templateRepository;
    private final UserRepository userRepository;
    

    @Override
    @Transactional
    public ContractTemplateDTO createTemplate(ContractTemplateCreateDTO dto, Long creatorId) throws ConflictException, ResourceNotFoundException {
        if (templateRepository.existsByName(dto.getName())) {
            throw new ConflictException("Template name already exists");
        }
        User creator = userRepository.findById(creatorId).orElseThrow(() -> new ResourceNotFoundException("Creator not found"));
        ContractTemplate t = new ContractTemplate();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setContent(dto.getContent());
        t.setCreator(creator);
        t.setCreatedAt(Instant.now());
        t.setUpdatedAt(Instant.now());
        ContractTemplate saved = templateRepository.save(t);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractTemplateDTO> listTemplatesByCreator(Long creatorId) {
        List<ContractTemplate> list = templateRepository.findByCreatorId(creatorId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ContractTemplateDTO mapToDTO(ContractTemplate t) {
        ContractTemplateDTO dto = new ContractTemplateDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setContent(t.getContent());
        dto.setCreatorId(t.getCreator() != null ? t.getCreator().getId() : null);
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        return dto;
    }
}
