package com.project.contracter.service;

import com.project.contracter.dtos.contractParticipant.ContractParticipantCreateDTO;
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
    public ContractParticipantDTO addParticipant(ContractParticipantCreateDTO dto, Long actorId) throws ResourceNotFoundException, ConflictException {
        // Only contract creator can add participants (simple rule)
        Contract c = contractRepository.findById(dto.getContractId()).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if (!c.getCreator().getId().equals(actorId)) {
            throw new ConflictException("Only creator can add participants");
        }
        User u = userRepository.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (participantRepository.existsByContractIdAndUserId(c.getId(), u.getId())) {
            throw new ConflictException("User already a participant");
        }
        ContractParticipant cp = new ContractParticipant();
        cp.setContract(c);
        cp.setUser(u);
        cp.setRole(dto.getRole());
        cp.setRequiredToSign(dto.getRequiredToSign() == null ? Boolean.FALSE : dto.getRequiredToSign());
        cp.setOrderToSign(dto.getOrderToSign());
        cp.setCreatedAt(Instant.now());
        ContractParticipant saved = participantRepository.save(cp);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractParticipantDTO> listParticipants(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        List<ContractParticipant> list = participantRepository.findByContractId(contractId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isUserSigner(Long contractId, Long userId) {
        Optional<ContractParticipant> opt = participantRepository.findByContractIdAndUserId(contractId, userId);
        return opt.map(cp -> cp.getRole() == ParticipantRole.SIGNER).orElse(false);
    }

    private ContractParticipantDTO mapToDTO(ContractParticipant cp) {
        ContractParticipantDTO dto = new ContractParticipantDTO();
        dto.setId(cp.getId());
        dto.setContractId(cp.getContract().getId());
        dto.setUserId(cp.getUser().getId());
        dto.setRole(cp.getRole());
        dto.setRequiredToSign(cp.getRequiredToSign());
        dto.setOrderToSign(cp.getOrderToSign());
        dto.setCreatedAt(cp.getCreatedAt());
        return dto;
    }
}
