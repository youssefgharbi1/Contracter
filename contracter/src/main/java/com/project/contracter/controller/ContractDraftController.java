package com.project.contracter.controller;


import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.service.serviceInterface.ContractDraftServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/drafts")
@RequiredArgsConstructor
public class ContractDraftController {

    private final ContractDraftServiceI draftService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContractDraftDTO>> createDraft(@PathVariable Long editorId,
                                                                     @RequestBody ContractDraftCreateDTO dto) {
        ContractDraftDTO draft = draftService.createDraft(dto, editorId);
        return ResponseEntity.ok(ApiResponse.<ContractDraftDTO>builder()
                .data(draft)
                .success(true)
                .message("Contract Draft Created successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractDraftDTO>>> getDrafts(@PathVariable Long contractId) {
        List<ContractDraftDTO> drafts = draftService.listDraftsForContract(contractId);
        return ResponseEntity.ok(ApiResponse.<List<ContractDraftDTO>>builder()
                .data(drafts)
                .success(true)
                .message("Contract Draft listed successfully")
                .build());
    }
    /*
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> getLatestDraft(@PathVariable Long contractId) {
        ContractDraftDTO draft = draftService.(contractId);
        return ResponseEntity.ok(ApiResponse.<List<ContractDraftDTO>>builder()
                .data(draft)
                .success(true)
                .message("latest Contract Draft found successfully")
                .build());
    }
    */

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ContractDraftDTO>> updateDraft(@PathVariable Long contractId,
                                                                     @RequestBody ContractDraftDTO dto) {
        ContractDraftDTO updated = draftService.updateDraft(contractId, dto);
        return ResponseEntity.ok(ApiResponse.<ContractDraftDTO>builder()
                .data(updated)
                .success(true)
                .message("Contract Draft Updated successfully")
                .build());
    }

    @DeleteMapping("/{version}")
    public ResponseEntity<ApiResponse<Void>> deleteDraft(@PathVariable Long contractId,
                                                         @PathVariable Integer version) {
        draftService.deleteDraft(contractId, version);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Contract Draft deleted successfully")
                .build());
    }
}
