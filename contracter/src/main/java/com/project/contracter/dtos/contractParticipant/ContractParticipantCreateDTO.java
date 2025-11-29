package com.project.contracter.dtos.contractParticipant;

// Adding participant
public class ContractParticipantCreateDTO {
    private Long contractId;
    private Long userId;
    private ParticipantRole role;
    private Boolean requiredToSign;
    private Integer orderToSign;
}
