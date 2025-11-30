package com.project.contracter.dtos.contractAttachment;

import lombok.Data;

@Data
public class ContractAttachmentCreateDTO {
    private Long contractId;
    private String filename;
    private String fileType;
    private String storageUrl; // or file bytes if handling upload
}
