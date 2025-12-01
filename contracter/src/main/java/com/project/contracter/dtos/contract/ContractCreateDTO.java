package com.project.contracter.dtos.contract;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ContractCreateDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String creatorUsername;
    private String content; // optional for initial draft
}
