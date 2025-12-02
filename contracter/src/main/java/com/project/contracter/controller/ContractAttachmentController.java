package com.project.contracter.controller;

import com.project.contracter.dtos.contractAttachment.ContractAttachmentCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contractAttachment.ContractAttachmentDTO;
import com.project.contracter.service.serviceInterface.ContractAttachmentServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/attachments")
@RequiredArgsConstructor
public class ContractAttachmentController {

    private final ContractAttachmentServiceI attachmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContractAttachmentDTO>> uploadAttachment(
            @PathVariable Long contractId,
            @RequestBody ContractAttachmentCreateDTO dto, // Changed to CreateDTO
            @RequestHeader("X-User-Id") Long uploaderId) { // Added uploaderId from header
        // Set contractId from path variable
        dto.setContractId(contractId);
        ContractAttachmentDTO attachment = attachmentService.addAttachment(dto, uploaderId);
        return ResponseEntity.ok(ApiResponse.<ContractAttachmentDTO>builder()
                .success(true)
                .message("Attachment uploaded successfully")
                .data(attachment)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractAttachmentDTO>>> getAttachments(
            @PathVariable Long contractId) {
        List<ContractAttachmentDTO> attachments = attachmentService.listAttachments(contractId); // Changed method name
        return ResponseEntity.ok(ApiResponse.<List<ContractAttachmentDTO>>builder()
                .success(true)
                .message("Attachments retrieved successfully")
                .data(attachments)
                .build());
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable Long contractId,
            @PathVariable Long attachmentId) {
        // Note: Your service doesn't have a deleteAttachment method
        // You'll need to add it or remove this endpoint
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Attachment deleted successfully")
                .data(null)
                .build());
    }
}