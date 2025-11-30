package com.project.contracter.service;

import com.project.contracter.dtos.contract.ContractCreateDTO;
import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.enums.ContractStatus;
import com.project.contracter.exception.BadRequestException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.exception.UnauthorizedException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractDraft;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractDraftRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.ContractServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService implements ContractServiceI {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final ContractDraftRepository draftRepository;

    @Override
    @Transactional
    public ContractDTO createContract(ContractCreateDTO dto, Long creatorId) throws ResourceNotFoundException {
        User creator = userRepository.findById(creatorId).orElseThrow(() -> new ResourceNotFoundException("Creator not found"));
        Contract c = new Contract();
        c.setTitle(dto.getTitle());
        c.setContent(dto.getContent());
        c.setStatus(ContractStatus.DRAFT);
        c.setCreator(creator);
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        Contract saved = contractRepository.save(c);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ContractDTO updateContract(Long contractId, ContractCreateDTO dto, Long userId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException {
        Contract c = contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if (!c.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only creator can update contract");
        }
        if (c.getStatus() == ContractStatus.PUBLISHED || c.getStatus() == ContractStatus.SIGNED || c.getStatus() == ContractStatus.COMPLETED) {
            throw new BadRequestException("Cannot update published or finalized contract");
        }
        c.setTitle(dto.getTitle());
        c.setContent(dto.getContent());
        c.setUpdatedAt(Instant.now());
        Contract updated = contractRepository.save(c);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public ContractDTO publishContract(Long contractId, Long userId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException {
        Contract c = contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if (!c.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only creator can publish the contract");
        }
        if (c.getStatus() == ContractStatus.PUBLISHED) {
            throw new BadRequestException("Contract already published");
        }
        // Optionally pick latest draft content if drafts exist
        Optional<Integer> latest = draftRepository.findLatestVersionByContractId(c.getId());
        if (latest.isPresent()) {
            List<ContractDraft> drafts = draftRepository.findByContractIdOrderByVersionDesc(c.getId());
            if (!drafts.isEmpty()) {
                c.setContent(drafts.get(0).getContent());
            }
        }
        c.setStatus(ContractStatus.PUBLISHED);
        c.setPublishedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        Contract saved = contractRepository.save(c);
        return mapToDTO(saved);
    }

    @Override
    public ContractDTO getContract(Long contractId) throws ResourceNotFoundException {
        Contract c = contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        return mapToDTO(c);
    }

    @Override
    public List<ContractDTO> listContractsByCreator(Long creatorId) {
        List<Contract> list = contractRepository.findByCreatorId(creatorId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ContractDTO mapToDTO(Contract c) {
        ContractDTO dto = new ContractDTO();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setContent(c.getContent());
        dto.setStatus(c.getStatus());
        dto.setCreatorId(c.getCreator() != null ? c.getCreator().getId() : null);
        dto.setCreatedAt(c.getCreatedAt());
        dto.setPublishedAt(c.getPublishedAt());
        return dto;
    }
}

