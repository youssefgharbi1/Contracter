package com.project.contracter.dtos.contractParticipant;

import com.project.contracter.enums.ParticipantRole;

import java.time.Instant;

// Returning participant info
public class ContractParticipantDTO {
    private Long id;
    private Long contractId;
    private Long userId;
    private ParticipantRole role;
    private Boolean requiredToSign;
    private Integer orderToSign;
    private Instant createdAt;
}

