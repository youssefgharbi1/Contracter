package com.project.contracter.dtos.contractDraft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ContractDraftDTO {
    private Long id;
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotBlank(message = "Content is required")
    private String content;
    @NotNull(message = "version is required")
    private Integer version;
    private Long editorId;
    private String editorUsername;
    private Instant createdAt;
}
