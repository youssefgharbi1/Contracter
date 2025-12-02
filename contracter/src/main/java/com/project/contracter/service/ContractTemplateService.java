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
    public ContractTemplateDTO getTemplateById(Long id) throws ResourceNotFoundException {
        ContractTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        return mapToDTO(template);
    }

    @Override
    public List<ContractTemplateDTO> listTemplatesByCreator(Long creatorId) {
        List<ContractTemplate> list = templateRepository.findByCreatorId(creatorId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ContractTemplateDTO> searchTemplates(String keyword) {
        List<ContractTemplate> list = templateRepository.searchByNameOrDescription(keyword);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractTemplateDTO updateTemplate(Long id, ContractTemplateCreateDTO dto) throws ResourceNotFoundException, ConflictException {
        ContractTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        // Check if name is being changed and if new name already exists (excluding current template)
        if (dto.getName() != null && !dto.getName().equals(template.getName())) {
            if (templateRepository.existsByName(dto.getName())) {
                throw new ConflictException("Template name already exists");
            }
            template.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            template.setDescription(dto.getDescription());
        }

        if (dto.getContent() != null) {
            template.setContent(dto.getContent());
        }

        template.setUpdatedAt(Instant.now());
        ContractTemplate updated = templateRepository.save(template);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) throws ResourceNotFoundException {
        if (!templateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return templateRepository.existsByName(name);
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