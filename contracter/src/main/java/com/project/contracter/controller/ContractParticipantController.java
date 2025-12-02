package com.project.contracter.controller;

import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contractParticipant.ContractParticipantDTO;
import com.project.contracter.service.serviceInterface.ContractParticipantServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/participants")
@RequiredArgsConstructor
public class ContractParticipantController {

    private final ContractParticipantServiceI participantService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ContractParticipantDTO>> addParticipant(
            @PathVariable Long contractId,
            @RequestBody ContractParticipantDTO dto) {
        ContractParticipantDTO created = participantService.addParticipant(dto, contractId);
        return ResponseEntity.ok(ApiResponse.<ContractParticipantDTO>builder()
                .success(true)
                .message("Participant added successfully")
                .data(created)
                .build());
    }

    @GetMapping
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
    public ResponseEntity<ApiResponse<List<ContractParticipantDTO>>> getRequiredSigners(
            @PathVariable Long contractId) {
        List<ContractParticipantDTO> signers = participantService.listSigners(contractId);
        return ResponseEntity.ok(ApiResponse.<List<ContractParticipantDTO>>builder()
                .success(true)
                .message("Required signers retrieved successfully")
                .data(signers)
                .build());
    }

    /*
    @PutMapping("/{participantId}")
    public ResponseEntity<ApiResponse<ContractParticipantDTO>> updateParticipant(
            @PathVariable Long contractId,
            @PathVariable Long participantId,
            @RequestBody ContractParticipantDTO dto) {
        ContractParticipantDTO updated = participantService.updateParticipant(contractId, participantId, dto);
        return ResponseEntity.ok(ApiResponse.<ContractParticipantDTO>builder()
                .success(true)
                .message("Participant updated successfully")
                .data(updated)
                .build());
    }
    */
    @DeleteMapping("/{participantId}")
    public ResponseEntity<ApiResponse<Void>> removeParticipant(@PathVariable Long contractId, @PathVariable Long participantId) {
        participantService.removeParticipant(contractId, participantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Participant removed successfully")
                .data(null)
                .build());
    }
}