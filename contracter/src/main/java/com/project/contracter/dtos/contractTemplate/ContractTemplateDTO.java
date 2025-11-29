package com.project.contracter.dtos.contractTemplate;

import java.time.Instant;

// Returning template info
public class ContractTemplateDTO {
    private Long id;
    private String name;
    private String description;
    private String content;
    private Long creatorId;
    private Instant createdAt;
    private Instant updatedAt;
}
