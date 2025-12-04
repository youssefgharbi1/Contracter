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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    @Override
    public ContractDTO createContract(ContractCreateDTO dto, Long creatorId) throws ResourceNotFoundException {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with ID: " + creatorId));

        Contract contract = new Contract();
        contract.setTitle(dto.getTitle());
        contract.setDescription(dto.getDescription());
        contract.setContent(dto.getContent());
        contract.setCategory(dto.getCategory());
        contract.setStatus(ContractStatus.DRAFT);
        contract.setCreator(creator);
        contract.setCreatedAt(Instant.now());
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);
        return mapToDTO(saved);
    }

    @Override
    public ContractDTO getContract(Long contractId) throws ResourceNotFoundException {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        return mapToDTO(contract);
    }

    @Transactional
    @Override
    public ContractDTO updateContract(Long contractId, ContractCreateDTO dto)
            throws ResourceNotFoundException, BadRequestException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        // Validate contract state
        if (contract.getStatus() == ContractStatus.PUBLISHED ||
                contract.getStatus() == ContractStatus.SIGNED ||
                contract.getStatus() == ContractStatus.COMPLETED) {
            throw new BadRequestException("Cannot update published or finalized contract");
        }

        // Update fields
        contract.setTitle(dto.getTitle());
        contract.setDescription(dto.getDescription());
        contract.setContent(dto.getContent());
        contract.setCategory(dto.getCategory());
        contract.setUpdatedAt(Instant.now());

        Contract updated = contractRepository.save(contract);
        return mapToDTO(updated);
    }

    @Transactional
    @Override
    public ContractDTO updateContractStatus(Long contractId, String status)
            throws ResourceNotFoundException, BadRequestException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        ContractStatus newStatus;
        try {
            newStatus = ContractStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }

        // Validate status transition
        validateStatusTransition(contract.getStatus(), newStatus);

        contract.setStatus(newStatus);
        contract.setUpdatedAt(Instant.now());

        // Set published date if publishing
        if (newStatus == ContractStatus.PUBLISHED && contract.getPublishedAt() == null) {
            contract.setPublishedAt(Instant.now());
        }

        Contract updated = contractRepository.save(contract);
        return mapToDTO(updated);
    }

    @Transactional
    @Override
    public void deleteContract(Long contractId, Long actorId)
            throws ResourceNotFoundException, UnauthorizedException, BadRequestException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        // Check if user is the creator
        if (!contract.getCreator().getId().equals(actorId)) {
            throw new UnauthorizedException("Only contract creator can delete the contract");
        }

        // Prevent deleting published or signed contracts
        if (contract.getStatus() == ContractStatus.PUBLISHED ||
                contract.getStatus() == ContractStatus.SIGNED ||
                contract.getStatus() == ContractStatus.COMPLETED) {
            throw new BadRequestException("Cannot delete published or finalized contract");
        }

        contractRepository.delete(contract);
    }

    @Override
    @Transactional
    public ContractDTO publishContract(Long contractId, Long userId)
            throws ResourceNotFoundException, UnauthorizedException, BadRequestException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        // Check if user is the creator
        if (!contract.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only contract creator can publish the contract");
        }

        // Check if already published
        if (contract.getStatus() == ContractStatus.PUBLISHED) {
            throw new BadRequestException("Contract already published");
        }

        // Optionally pick latest draft content if drafts exist
        Optional<ContractDraft> latestDraft = draftRepository
                .findByContractIdOrderByVersionDesc(contractId)
                .stream()
                .findFirst();

        if (latestDraft.isPresent()) {
            contract.setContent(latestDraft.get().getContent());
        }

        contract.setStatus(ContractStatus.PUBLISHED);
        contract.setPublishedAt(Instant.now());
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);
        return mapToDTO(saved);
    }

    @Transactional
    @Override
    public ContractDTO archiveContract(Long contractId, Long userId)
            throws ResourceNotFoundException, UnauthorizedException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        // Check if user is the creator
        if (!contract.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only contract creator can archive the contract");
        }

        contract.setStatus(ContractStatus.ARCHIVED);
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);
        return mapToDTO(saved);
    }

    @Transactional
    @Override
    public ContractDTO unarchiveContract(Long contractId, Long userId)
            throws ResourceNotFoundException, UnauthorizedException, BadRequestException {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        // Check if user is the creator
        if (!contract.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only contract creator can unarchive the contract");
        }

        if (contract.getStatus() != ContractStatus.ARCHIVED) {
            throw new BadRequestException("Contract is not archived");
        }

        contract.setStatus(ContractStatus.DRAFT);
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractDTO> listContractsByCreator(Long creatorId) {
        List<Contract> contracts = contractRepository.findByCreatorId(creatorId);
        return contracts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDTO> listContractsByParticipant(Long userId) {
        List<Contract> contracts = contractRepository.findContractsByParticipantId(userId);
        return contracts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContractDTO> listAllContracts(Pageable pageable, String status, String search) {
        Page<Contract> contracts;

        if (status != null && search != null) {
            ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
            contracts = contractRepository.findByStatusAndTitleContainingIgnoreCase(contractStatus, search, pageable);
        } else if (status != null) {
            ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
            contracts = contractRepository.findByStatus(contractStatus, pageable);
        } else if (search != null) {
            contracts = contractRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                    search, pageable);
        } else {
            contracts = contractRepository.findAll(pageable);
        }

        return contracts.map(this::mapToDTO);
    }

    @Override
    public List<ContractDTO> getContractHistory(Long contractId) throws ResourceNotFoundException {
        // For now, return empty list or implement simple version history
        // You can implement this later when you add proper history tracking
        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));

        return List.of();
    }

    @Override
    public List<ContractDTO> searchContracts(String query, String scope, Long userId) {
        List<Contract> contracts;

        switch (scope.toLowerCase()) {
            case "my-contracts":
                contracts = contractRepository.searchByCreator(userId, query);
                break;
            case "participating":
                contracts = contractRepository.searchByParticipant(userId, query);
                break;
            case "all":
                contracts = contractRepository.searchAll(query);
                break;
            default:
                contracts = contractRepository.searchAll(query);
        }

        return contracts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Object getContractStats(Long userId) {
        // Count contracts by status for the user
        long draftCount = contractRepository.countByCreatorIdAndStatus(userId, ContractStatus.DRAFT);
        long publishedCount = contractRepository.countByCreatorIdAndStatus(userId, ContractStatus.PUBLISHED);
        long signedCount = contractRepository.countByCreatorIdAndStatus(userId, ContractStatus.SIGNED);
        long completedCount = contractRepository.countByCreatorIdAndStatus(userId, ContractStatus.COMPLETED);
        long archivedCount = contractRepository.countByCreatorIdAndStatus(userId, ContractStatus.ARCHIVED);

        // Count contracts where user participates
        long participatingCount = contractRepository.countContractsByParticipantId(userId);

        return new ContractStats(
                draftCount,
                publishedCount,
                signedCount,
                completedCount,
                archivedCount,
                participatingCount,
                draftCount + publishedCount + signedCount + completedCount + archivedCount
        );
    }

    // Helper methods
    private void validateStatusTransition(ContractStatus current, ContractStatus next) throws BadRequestException {
        // Add your business rules for status transitions here
        // Example: Can't go from COMPLETED back to DRAFT
        if (current == ContractStatus.COMPLETED && next == ContractStatus.DRAFT) {
            throw new BadRequestException("Cannot move completed contract back to draft");
        }

        if (current == ContractStatus.ARCHIVED && next != ContractStatus.DRAFT) {
            throw new BadRequestException("Archived contracts can only be moved to draft");
        }
    }

    private ContractDTO mapToDTO(Contract contract) {
        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setTitle(contract.getTitle());
        dto.setDescription(contract.getDescription());
        dto.setContent(contract.getContent());
        dto.setCategory(contract.getCategory());
        dto.setStatus(contract.getStatus());
        dto.setCreatorId(contract.getCreator() != null ? contract.getCreator().getId() : null);
        dto.setCreatorName(contract.getCreator() != null ?
                contract.getCreator().getFirstName() + " " + contract.getCreator().getLastName() : null);
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());
        dto.setPublishedAt(contract.getPublishedAt());
        return dto;
    }

    // Inner class for stats
    private static class ContractStats {
        private long draftCount;
        private long publishedCount;
        private long signedCount;
        private long completedCount;
        private long archivedCount;
        private long participatingCount;
        private long totalCreatedCount;

        public ContractStats(long draftCount, long publishedCount, long signedCount,
                             long completedCount, long archivedCount, long participatingCount,
                             long totalCreatedCount) {
            this.draftCount = draftCount;
            this.publishedCount = publishedCount;
            this.signedCount = signedCount;
            this.completedCount = completedCount;
            this.archivedCount = archivedCount;
            this.participatingCount = participatingCount;
            this.totalCreatedCount = totalCreatedCount;
        }

    }
}