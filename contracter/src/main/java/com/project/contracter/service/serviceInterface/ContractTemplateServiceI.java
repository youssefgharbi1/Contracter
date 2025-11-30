package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractTemplate.ContractTemplateCreateDTO;
import com.project.contracter.dtos.contractTemplate.ContractTemplateDTO;
import com.project.contracter.exception.ConflictException;

import java.util.List;

public interface ContractTemplateServiceI {
    ContractTemplateDTO createTemplate(ContractTemplateCreateDTO dto, Long creatorId) throws ConflictException;
    List<ContractTemplateDTO> listTemplatesByCreator(Long creatorId);
}
