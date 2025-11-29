package com.project.contracter.dtos.contractAttachment;

import java.time.Instant;

// Returning attachment info
public class ContractAttachmentDTO {
    private Long id;
    private Long contractId;
    private String filename;
    private String fileType;
    private String storageUrl;
    private Long uploadedById;
    private Instant uploadedAt;
}
