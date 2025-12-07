package com.project.contracter.service;

import com.project.contracter.enums.ContractStatus;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractParticipantRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.security.user.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractSecurityService {

    private final ContractRepository contractRepository;
    private final ContractParticipantRepository participantRepository;
    private final UserProfileService userProfileService;
    private final ContractParticipantService participantService;

    public boolean isContractCreator(Long contractId) {
        try {
            User currentUser = userProfileService.getCurrentUser();
            return participantService.isUserContractCreator(contractId, currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canViewContract(Long contractId) {
        try {
            Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
            if (contract.getStatus() == ContractStatus.PUBLISHED) { return true;}
            User currentUser = userProfileService.getCurrentUser();

            // Check if user is contract creator
            if (participantService.isUserContractCreator(contractId, currentUser.getId())) {
                return true;
            }

            // Check if user is a participant
            return participantService.isUserParticipant(contractId, currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canSignContract(Long contractId) {
        try {
            User currentUser = userProfileService.getCurrentUser();
            return participantService.isUserSigner(contractId, currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canEditContract(Long contractId) {
        try {
            User currentUser = userProfileService.getCurrentUser();
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            return participantService.isUserContractEditor(contractId, currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }
}