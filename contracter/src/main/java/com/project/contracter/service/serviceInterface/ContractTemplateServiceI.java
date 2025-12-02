package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractTemplate.ContractTemplateCreateDTO;
import com.project.contracter.dtos.contractTemplate.ContractTemplateDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;

import java.util.List;

public interface ContractTemplateServiceI {
    ContractTemplateDTO createTemplate(ContractTemplateCreateDTO dto, Long creatorId) throws ConflictException, ResourceNotFoundException;
    ContractTemplateDTO getTemplateById(Long id) throws ResourceNotFoundException;
    List<ContractTemplateDTO> listTemplatesByCreator(Long creatorId);
    List<ContractTemplateDTO> searchTemplates(String keyword);
    ContractTemplateDTO updateTemplate(Long id, ContractTemplateCreateDTO dto) throws ResourceNotFoundException, ConflictException;
    void deleteTemplate(Long id) throws ResourceNotFoundException;
    boolean existsByName(String name);
}