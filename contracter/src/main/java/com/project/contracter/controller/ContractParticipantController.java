package com.project.contracter.controller;

import com.project.contracter.dtos.contractParticipant.ContractParticipantDTO;
import com.project.contracter.model.User;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.security.user.UserProfileService;
import com.project.contracter.service.serviceInterface.ContractParticipantServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/participants")
@RequiredArgsConstructor
public class ContractParticipantController {

    private final ContractParticipantServiceI participantService;
    private final UserProfileService userProfileService;

    @PostMapping("/add")
    @PreAuthorize("@contractSecurityService.isContractCreator(#contractId)")
    public ResponseEntity<ApiResponse<ContractParticipantDTO>> addParticipant(
            @PathVariable Long contractId,
            @RequestBody ContractParticipantDTO dto) {

        // Get current logged-in user
        User currentUser = userProfileService.getCurrentUser();

        // Set the contractId from path variable
        dto.setContractId(contractId);

        ContractParticipantDTO created = participantService.addParticipant(dto, currentUser.getId());

        return ResponseEntity.ok(ApiResponse.<ContractParticipantDTO>builder()
                .success(true)
                .message("Participant added successfully")
                .data(created)
                .build());
    }

    @GetMapping
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<List<ContractParticipantDTO>>> getParticipants(
            @PathVariable Long contractId) {

        List<ContractParticipantDTO> participants = participantService.listParticipants(contractId);

        return ResponseEntity.ok(ApiResponse.<List<ContractParticipantDTO>>builder()
                .success(true)
                .message("Participants retrieved successfully")
                .data(participants)
                .build());
    }

    @GetMapping("/required-signers")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<List<ContractParticipantDTO>>> getRequiredSigners(
            @PathVariable Long contractId) {

        List<ContractParticipantDTO> signers = participantService.listSigners(contractId);

        return ResponseEntity.ok(ApiResponse.<List<ContractParticipantDTO>>builder()
                .success(true)
                .message("Required signers retrieved successfully")
                .data(signers)
                .build());
    }

    @GetMapping("/{participantId}")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractParticipantDTO>> getParticipant(
            @PathVariable Long contractId,
            @PathVariable Long participantId) {

        ContractParticipantDTO participant = participantService.getParticipant(contractId, participantId);

        return ResponseEntity.ok(ApiResponse.<ContractParticipantDTO>builder()
                .success(true)
                .message("Participant retrieved successfully")
                .data(participant)
                .build());
    }

    @DeleteMapping("/{participantId}")
    @PreAuthorize("@contractSecurityService.isContractCreator(#contractId)")
    public ResponseEntity<ApiResponse<Void>> removeParticipant(
            @PathVariable Long contractId,
            @PathVariable Long participantId) {

        participantService.removeParticipant(contractId, participantId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Participant removed successfully")
                .data(null)
                .build());
    }

    @GetMapping("/check-signer/{userId}")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<Boolean>> isUserSigner(
            @PathVariable Long contractId,
            @PathVariable Long userId) {

        boolean isSigner = participantService.isUserSigner(contractId, userId);

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("Signer check completed")
                .data(isSigner)
                .build());
    }
}