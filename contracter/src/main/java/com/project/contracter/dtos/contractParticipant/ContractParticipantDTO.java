package com.project.contracter.dtos.contractParticipant;

import com.project.contracter.enums.ParticipantRole;

import java.time.Instant;

import lombok.Data;

@Data
public class ContractParticipantDTO {
    private Long id;
    private Long contractId;
    private Long userId;
    private ParticipantRole role;
    private Boolean requiredToSign;
    private Integer orderToSign;
    private Instant createdAt;
}

