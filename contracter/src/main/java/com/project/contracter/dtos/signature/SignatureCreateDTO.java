package com.project.contracter.dtos.signature;

// Creating a signature
public class SignatureCreateDTO {
    private Long contractId;
    private Long userId; // optional if retrieved from auth context
    private SignatureType signatureType;
}
