package com.project.contracter.dtos.contractAttachment;

import java.time.Instant;

import lombok.Data;

@Data
public class ContractAttachmentDTO {
    private Long id;
    private Long contractId;
    private String filename;
    private String fileType;
    private String storageUrl;
    private Long uploadedById;
    private Instant uploadedAt;
}
