package com.project.contracter.dtos.contractParticipant;

import com.project.contracter.enums.ParticipantRole;
import lombok.Data;

@Data
public class ContractParticipantCreateDTO {
    private Long contractId;
    private Long userId;
    private ParticipantRole role;
    private Boolean requiredToSign;
    private Integer orderToSign;
}
