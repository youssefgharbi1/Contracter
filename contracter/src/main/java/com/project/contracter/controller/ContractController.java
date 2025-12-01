package com.project.contracter.controller;

import com.project.contracter.dtos.contract.ContractCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.service.serviceInterface.ContractServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractServiceI contractService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContractDTO>> createContract(@RequestBody ContractCreateDTO dto) {
        ContractDTO created = contractService.createContract(dto);
        return ResponseEntity.ok(ApiResponse.<ContractDTO>builder()
                .data(created)
                .success(true)
                .message("Contract Create successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDTO>> getContract(@PathVariable Long id) {
        ContractDTO contract = contractService.getContract(id);
        return ResponseEntity.ok(ApiResponse.<ContractDTO>builder()
                .data(contract)
                .success(true)
                .message("Contract found successfully")
                .build());
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getByCreator(@PathVariable Long userId) {
        List<ContractDTO> contracts = contractService.listContractsByCreator(userId);
        return ResponseEntity.ok(ApiResponse.<List<ContractDTO>>builder()
                .data(contracts)
                .success(true)
                .message("Contracts found successfully")
                .build());
    }

    @GetMapping("/participant/{userId}")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getForParticipant(@PathVariable Long userId) {
        List<ContractDTO> contracts = contractService.listContractsByParticipant(userId);
        return ResponseEntity.ok(ApiResponse.<List<ContractDTO>>builder()
                .data(contracts)
                .success(true)
                .message("Contracts found successfully")
                .build());
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ContractDTO>> publishContract(@PathVariable Long id) {
        ContractDTO published = contractService.publishContract(id, contractService.getContract(id).getCreatorId());
        return ResponseEntity.ok(ApiResponse.<ContractDTO>builder()
                .success(true)
                .data(published)
                .message("Contract published successfully")
                .build());
    }
    /*
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ContractDTO>> archiveContract(@PathVariable Long id) {
        ContractDTO archived = contractService.archiveContract(id);
        return ResponseEntity.ok(ApiResponse.<ContractDTO>builder()
                .success(true)
                .message("Contract Create successfully")
                .build());
    }
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Contract Create successfully")
                .build());
    }
}
