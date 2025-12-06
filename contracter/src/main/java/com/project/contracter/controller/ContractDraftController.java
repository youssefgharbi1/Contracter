package com.project.contracter.controller;

import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.model.User;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.security.user.UserProfileService;
import com.project.contracter.service.serviceInterface.ContractDraftServiceI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/drafts")
@RequiredArgsConstructor
public class ContractDraftController {

    private final ContractDraftServiceI draftService;
    private final UserProfileService userProfileService;

    @PostMapping
    @PreAuthorize("@contractSecurityService.canEditContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> createDraft(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractDraftCreateDTO dto) {

        // Get current logged-in user
        User currentUser = userProfileService.getCurrentUser();

        // Set contract ID from path variable
        dto.setContractId(contractId);

        ContractDraftDTO draft = draftService.createDraft(dto, currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Draft created successfully", draft));
    }

    @GetMapping
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<List<ContractDraftDTO>>> getDrafts(
            @PathVariable Long contractId) {

        List<ContractDraftDTO> drafts = draftService.listDraftsForContract(contractId);

        return ResponseEntity.ok(
                ApiResponse.success("Drafts retrieved successfully", drafts));
    }

    @GetMapping("/latest")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> getLatestDraft(
            @PathVariable Long contractId) {

        ContractDraftDTO draft = draftService.getLatestDraft(contractId);

        return ResponseEntity.ok(
                ApiResponse.success("Latest draft retrieved successfully", draft));
    }

    @GetMapping("/{draftId}")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> getDraft(
            @PathVariable Long contractId,
            @PathVariable Long draftId) {

        ContractDraftDTO draft = draftService.getDraft(draftId);

        // Verify draft belongs to this contract
        if (!draft.getContractId().equals(contractId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Draft not found in this contract"));
        }

        return ResponseEntity.ok(
                ApiResponse.success("Draft retrieved successfully", draft));
    }

    @GetMapping("/version/{version}")
    @PreAuthorize("@contractSecurityService.canViewContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> getDraftByVersion(
            @PathVariable Long contractId,
            @PathVariable Integer version) {

        ContractDraftDTO draft = draftService.getDraftByVersion(contractId, version);

        return ResponseEntity.ok(
                ApiResponse.success("Draft version retrieved successfully", draft));
    }

    @PutMapping("/{draftId}")
    @PreAuthorize("@contractSecurityService.canEditContract(#contractId)")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> updateDraft(
            @PathVariable Long contractId,
            @PathVariable Long draftId,
            @Valid @RequestBody ContractDraftDTO dto) {

        // Ensure consistency
        dto.setId(draftId);
        dto.setContractId(contractId);

        ContractDraftDTO updated = draftService.updateDraft(contractId, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Draft updated successfully", updated));
    }

    @DeleteMapping("/{draftId}")
    @PreAuthorize("@contractSecurityService.isContractCreator(#contractId)")
    public ResponseEntity<ApiResponse<Void>> deleteDraftById(
            @PathVariable Long draftId) {

        draftService.deleteDraftById(draftId);

        return ResponseEntity.ok(
                ApiResponse.success("Draft deleted successfully", null));
    }

    @DeleteMapping("/version/{version}")
    @PreAuthorize("@contractSecurityService.isContractCreator(#contractId)")
    public ResponseEntity<ApiResponse<Void>> deleteDraftByVersion(
            @PathVariable Long contractId,
            @PathVariable Integer version) {

        draftService.deleteDraftByVersion(contractId, version);

        return ResponseEntity.ok(
                ApiResponse.success("Draft version deleted successfully", null));
    }
}