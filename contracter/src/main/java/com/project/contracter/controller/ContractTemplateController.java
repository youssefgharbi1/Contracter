package com.project.contracter.controller;

import com.project.contracter.dtos.contractTemplate.ContractTemplateCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.contractTemplate.ContractTemplateDTO;
import com.project.contracter.service.serviceInterface.ContractTemplateServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class ContractTemplateController {

    private final ContractTemplateServiceI templateService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContractTemplateDTO>> createTemplate(
            @RequestBody ContractTemplateCreateDTO dto,
            @RequestHeader("X-User-Id") Long creatorId) {
        ContractTemplateDTO created = templateService.createTemplate(dto, creatorId);
        return ResponseEntity.ok(ApiResponse.<ContractTemplateDTO>builder()
                .success(true)
                .message("Template created successfully")
                .data(created)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractTemplateDTO>> getTemplate(@PathVariable Long id) {
        ContractTemplateDTO template = templateService.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.<ContractTemplateDTO>builder()
                .success(true)
                .message("Template retrieved successfully")
                .data(template)
                .build());
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<ApiResponse<List<ContractTemplateDTO>>> getByCreator(@PathVariable Long userId) {
        List<ContractTemplateDTO> templates = templateService.listTemplatesByCreator(userId);
        return ResponseEntity.ok(ApiResponse.<List<ContractTemplateDTO>>builder()
                .success(true)
                .message("Templates retrieved successfully")
                .data(templates)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ContractTemplateDTO>>> searchTemplates(@RequestParam String keyword) {
        List<ContractTemplateDTO> templates = templateService.searchTemplates(keyword);
        return ResponseEntity.ok(ApiResponse.<List<ContractTemplateDTO>>builder()
                .success(true)
                .message("Templates found successfully")
                .data(templates)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @RequestBody ContractTemplateCreateDTO dto) {
        ContractTemplateDTO updated = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(ApiResponse.<ContractTemplateDTO>builder()
                .success(true)
                .message("Template updated successfully")
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Template deleted successfully")
                .data(null)
                .build());
    }
}