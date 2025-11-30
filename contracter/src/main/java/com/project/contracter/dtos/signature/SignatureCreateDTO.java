package com.project.contracter.dtos.signature;

import com.project.contracter.enums.SignatureType;
import lombok.Data;

@Data
public class SignatureCreateDTO {
    private Long contractId;
    private Long userId; // optional if retrieved from auth context
    private SignatureType signatureType;
}
