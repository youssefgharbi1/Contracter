package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractParticipant.ContractParticipantCreateDTO;
import com.project.contracter.dtos.contractParticipant.ContractParticipantDTO;
import com.project.contracter.exception.ResourceNotFoundException;

import java.util.List;

public interface ContractParticipantServiceI {
    ContractParticipantDTO addParticipant(ContractParticipantCreateDTO dto, Long actorId) throws ResourceNotFoundException, ConflictException;
    List<ContractParticipantDTO> listParticipants(Long contractId) throws ResourceNotFoundException;
    boolean isUserSigner(Long contractId, Long userId);
}
