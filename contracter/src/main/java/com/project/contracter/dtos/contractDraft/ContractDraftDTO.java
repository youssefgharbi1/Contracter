package com.project.contracter.dtos.contractDraft;

import lombok.Data;

import java.time.Instant;

@Data
public class ContractDraftDTO {
    private Long id;
    private Long contractId;
    private Integer version;
    private String content;
    private Long editorId;
    private Instant createdAt;
}
