package com.project.contracter.service;

import com.project.contracter.dtos.contractAttachment.ContractAttachmentCreateDTO;
import com.project.contracter.dtos.contractAttachment.ContractAttachmentDTO;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractAttachment;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractAttachmentRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.ContractAttachmentServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractAttachmentService implements ContractAttachmentServiceI {

    private final ContractAttachmentRepository attachmentRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    

    @Override
    @Transactional
    public ContractAttachmentDTO addAttachment(ContractAttachmentCreateDTO dto, Long uploaderId) throws ResourceNotFoundException {
        Contract c = contractRepository.findById(dto.getContractId()).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        User uploader = userRepository.findById(uploaderId).orElseThrow(() -> new ResourceNotFoundException("Uploader not found"));
        ContractAttachment a = new ContractAttachment();
        a.setContract(c);
        a.setFilename(dto.getFilename());
        a.setFileType(dto.getFileType());
        a.setStorageUrl(dto.getStorageUrl());
        a.setUploadedBy(uploader);
        a.setUploadedAt(Instant.now());
        ContractAttachment saved = attachmentRepository.save(a);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractAttachmentDTO> listAttachments(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        List<ContractAttachment> list = attachmentRepository.findByContractId(contractId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ContractAttachmentDTO mapToDTO(ContractAttachment a) {
        ContractAttachmentDTO dto = new ContractAttachmentDTO();
        dto.setId(a.getId());
        dto.setContractId(a.getContract().getId());
        dto.setFilename(a.getFilename());
        dto.setFileType(a.getFileType());
        dto.setStorageUrl(a.getStorageUrl());
        dto.setUploadedById(a.getUploadedBy() != null ? a.getUploadedBy().getId() : null);
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
