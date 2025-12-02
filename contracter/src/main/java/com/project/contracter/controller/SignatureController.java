package com.project.contracter.controller;

import com.project.contracter.dtos.signature.SignatureCreateDTO;
import com.project.contracter.response.ApiResponse;
import com.project.contracter.dtos.signature.SignatureDTO;
import com.project.contracter.service.serviceInterface.SignatureServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{contractId}/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureServiceI signatureService;

    @PostMapping("/{userId}/sign")
    public ResponseEntity<ApiResponse<SignatureDTO>> signContract(
            @PathVariable Long contractId,
            @PathVariable Long userId,
            @RequestBody SignatureCreateDTO dto) { // Changed to SignatureCreateDTO
        // Set contractId from path variable
        dto.setContractId(contractId);
        SignatureDTO signed = signatureService.signContract(dto, userId);
        return ResponseEntity.ok(ApiResponse.<SignatureDTO>builder()
                .success(true)
                .message("Contract signed successfully")
                .data(signed)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SignatureDTO>>> getSignatures(@PathVariable Long contractId) {
        List<SignatureDTO> signatures = signatureService.listSignatures(contractId); // Changed method name
        return ResponseEntity.ok(ApiResponse.<List<SignatureDTO>>builder()
                .success(true)
                .message("Signatures retrieved successfully")
                .data(signatures)
                .build());
    }


}