package com.project.contracter.service;

import com.project.contracter.dtos.contractParticipant.ContractParticipantDTO;
import com.project.contracter.enums.ParticipantRole;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractParticipant;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractParticipantRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.ContractParticipantServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractParticipantService implements ContractParticipantServiceI {

    private final ContractParticipantRepository participantRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ContractParticipantDTO addParticipant(ContractParticipantDTO dto, Long actorId) throws ResourceNotFoundException, ConflictException {
        // Only contract creator can add participants (simple rule)
        Contract c = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        if (!c.getCreator().getId().equals(actorId)) {
            throw new ConflictException("Only creator can add participants");
        }

        User u = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (participantRepository.existsByContractIdAndUserId(c.getId(), u.getId())) {
            throw new ConflictException("User already a participant");
        }

        // Prevent adding creator as participant
        if (u.getId().equals(actorId)) {
            throw new ConflictException("Creator cannot be added as a participant");
        }

        ContractParticipant cp = new ContractParticipant();
        cp.setContract(c);
        cp.setUser(u);
        cp.setRole(dto.getRole());
        cp.setRequiredToSign(dto.getRequiredToSign() == null ? Boolean.FALSE : dto.getRequiredToSign());
        cp.setCreatedAt(Instant.now());
        cp.setSigned(false); // Default to not signed

        ContractParticipant saved = participantRepository.save(cp);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractParticipantDTO> listParticipants(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        List<ContractParticipant> list = participantRepository.findByContractId(contractId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ContractParticipantDTO> listSigners(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        List<ContractParticipant> list = participantRepository.findByContractId(contractId);
        return list.stream()
                .filter(cp -> cp.getRole() == ParticipantRole.SIGNER)
                .filter(cp -> Boolean.TRUE.equals(cp.getRequiredToSign())) // Only required signers
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeParticipant(Long contractId, Long participantId) throws ResourceNotFoundException {
        // First verify contract exists
        if (!contractRepository.existsById(contractId)) {
            throw new ResourceNotFoundException("Contract not found");
        }

        // Find the participant
        ContractParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        // Verify participant belongs to this contract
        if (!participant.getContract().getId().equals(contractId)) {
            throw new ResourceNotFoundException("Participant not found in this contract");
        }

        // Prevent removing SIGNERs who have already signed
        if (participant.getRole() == ParticipantRole.SIGNER &&
                Boolean.TRUE.equals(participant.getSigned())) {
            throw new ConflictException("Cannot remove a signer who has already signed");
        }

        participantRepository.delete(participant);
    }

    @Override
    public boolean isUserSigner(Long contractId, Long userId) {
        Optional<ContractParticipant> opt = participantRepository.findByContractIdAndUserId(contractId, userId);
        return opt.map(cp -> cp.getRole() == ParticipantRole.SIGNER
                        && Boolean.TRUE.equals(cp.getRequiredToSign()))
                .orElse(false);
    }

    // New method to check if user is a participant (any role)
    @Override
    public boolean isUserParticipant(Long contractId, Long userId) {
        return participantRepository.existsByContractIdAndUserId(contractId, userId);
    }

    // New method to check if user is contract creator
    @Override
    public boolean isUserContractCreator(Long contractId, Long userId) throws ResourceNotFoundException {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        return contract.getCreator().getId().equals(userId);
    }
    @Override
    public boolean isUserContractEditor(Long contractId, Long userId) throws ResourceNotFoundException {
        ContractParticipant cEditor = participantRepository.findByContractIdAndUserId(contractId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        return cEditor.getRole().equals(ParticipantRole.EDITOR) ||  cEditor.getRole().equals(ParticipantRole.CREATOR);

        }

    // New method to get participant by ID with contract validation
    @Override
    public ContractParticipantDTO getParticipant(Long contractId, Long participantId) throws ResourceNotFoundException {
        ContractParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        if (!participant.getContract().getId().equals(contractId)) {
            throw new ResourceNotFoundException("Participant not found in this contract");
        }

        return mapToDTO(participant);
    }

    private ContractParticipantDTO mapToDTO(ContractParticipant cp) {
        ContractParticipantDTO dto = new ContractParticipantDTO();
        dto.setId(cp.getId());
        dto.setContractId(cp.getContract().getId());
        dto.setUserId(cp.getUser().getId());
        dto.setRole(cp.getRole());
        dto.setRequiredToSign(cp.getRequiredToSign());
        dto.setCreatedAt(cp.getCreatedAt());
        dto.setSigned(cp.getSigned());


        return dto;
    }
}