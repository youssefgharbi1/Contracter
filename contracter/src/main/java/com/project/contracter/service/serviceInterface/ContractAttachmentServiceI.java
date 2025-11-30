package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractAttachment.ContractAttachmentCreateDTO;
import com.project.contracter.dtos.contractAttachment.ContractAttachmentDTO;
import com.project.contracter.exception.ResourceNotFoundException;

import java.util.List;

public interface ContractAttachmentServiceI {
    ContractAttachmentDTO addAttachment(ContractAttachmentCreateDTO dto, Long uploaderId) throws ResourceNotFoundException;
    List<ContractAttachmentDTO> listAttachments(Long contractId) throws ResourceNotFoundException;
}
