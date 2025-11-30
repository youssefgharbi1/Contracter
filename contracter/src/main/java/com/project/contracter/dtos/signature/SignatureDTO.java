package com.project.contracter.dtos.signature;

import com.project.contracter.enums.SignatureType;

import java.time.Instant;

import lombok.Data;

@Data
public class SignatureDTO {
    private Long id;
    private Long contractId;
    private Long userId;
    private String signatureValue;
    private SignatureType signatureType;
    private Instant signedAt;
}
