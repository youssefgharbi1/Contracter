package com.project.contracter.dtos.contract;

import com.project.contracter.enums.ContractCategory;
import com.project.contracter.enums.ContractStatus;

import java.time.Instant;

import lombok.Data;

@Data
public class ContractDTO {

    private Long id;
    private String title;
    private String content;
    private ContractCategory category;
    private String description;
    private ContractStatus status;
    private Long creatorId;
    private String creatorName;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
}
