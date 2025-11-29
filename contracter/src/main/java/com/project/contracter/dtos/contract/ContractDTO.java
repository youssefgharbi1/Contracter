package com.project.contracter.dtos.contract;

import com.project.contracter.enums.ContractStatus;

import java.time.Instant;

// Returning contract info
public class ContractDTO {
    private Long id;
    private String title;
    private String content;
    private ContractStatus status;
    private Long creatorId;
    private Instant createdAt;
    private Instant publishedAt;
}
