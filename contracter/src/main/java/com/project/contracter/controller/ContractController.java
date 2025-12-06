package com.project.contracter.controller;

import com.project.contracter.dtos.contract.ContractCreateDTO;
import com.project.contracter.dtos.contract.ContractUpdateDTO;
import com.project.contracter.model.User;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.security.user.UserProfileService;
import com.project.contracter.service.serviceInterface.ContractServiceI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractServiceI contractService;
    private final UserProfileService userProfileService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ContractDTO>> createContract(
            @Valid @RequestBody ContractCreateDTO dto) {

        // Get current logged-in user
        User currentUser = userProfileService.getCurrentUser();

        ContractDTO created = contractService.createContract(dto, currentUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contract created successfully", created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@contractSecurityService.canViewContract(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> getContract(@PathVariable Long id) {
        ContractDTO contract = contractService.getContract(id);

        return ResponseEntity.ok(
                ApiResponse.success("Contract retrieved successfully", contract));
    }

    @GetMapping("/my-contracts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getMyContracts() {
        User currentUser = userProfileService.getCurrentUser();
        List<ContractDTO> contracts = contractService.listContractsByCreator(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("My contracts retrieved successfully", contracts));
    }

    @GetMapping("/participating")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getContractsIParticipateIn() {
        User currentUser = userProfileService.getCurrentUser();
        List<ContractDTO> contracts = contractService.listContractsByParticipant(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contracts I participate in retrieved successfully", contracts));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getAllContracts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Page<ContractDTO> contracts = contractService.listAllContracts(pageable, status, search);

        return ResponseEntity.ok(
                ApiResponse.success("All contracts retrieved successfully", contracts));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@contractSecurityService.canEditContract(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractUpdateDTO dto) {

        ContractDTO updated = contractService.updateContract(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success("Contract updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@contractSecurityService.canEditContract(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> updateContractStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        ContractDTO updated = contractService.updateContractStatus(id, status);

        return ResponseEntity.ok(
                ApiResponse.success("Contract status updated successfully", updated));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@contractSecurityService.isContractCreator(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> publishContract(@PathVariable Long id) {
        User currentUser = userProfileService.getCurrentUser();
        ContractDTO published = contractService.publishContract(id, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contract published successfully", published));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("@contractSecurityService.isContractCreator(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> archiveContract(@PathVariable Long id) {
        User currentUser = userProfileService.getCurrentUser();
        ContractDTO archived = contractService.archiveContract(id, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contract archived successfully", archived));
    }

    @PostMapping("/{id}/unarchive")
    @PreAuthorize("@contractSecurityService.isContractCreator(#id)")
    public ResponseEntity<ApiResponse<ContractDTO>> unarchiveContract(@PathVariable Long id) {
        User currentUser = userProfileService.getCurrentUser();
        ContractDTO unarchived = contractService.unarchiveContract(id, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contract unarchived successfully", unarchived));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@contractSecurityService.isContractCreator(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable Long id) {
        User currentUser = userProfileService.getCurrentUser();
        contractService.deleteContract(id, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contract deleted successfully", null));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("@contractSecurityService.canViewContract(#id)")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getContractHistory(@PathVariable Long id) {
        List<ContractDTO> history = contractService.getContractHistory(id);

        return ResponseEntity.ok(
                ApiResponse.success("Contract history retrieved successfully", history));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> searchContracts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "all") String scope) {

        User currentUser = userProfileService.getCurrentUser();
        List<ContractDTO> results = contractService.searchContracts(query, scope, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Search results retrieved successfully", results));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Object>> getContractStats() {
        User currentUser = userProfileService.getCurrentUser();
        Object stats = contractService.getContractStats(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Contract statistics retrieved successfully", stats));
    }
}