package com.project.contracter.dtos.contractDraft;

// Returning draft info
public class ContractDraftDTO {
    private Long id;
    private Long contractId;
    private Integer version;
    private String content;
    private Long editorId;
    private Instant createdAt;
}
