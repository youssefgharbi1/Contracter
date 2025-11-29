package com.project.contracter.dtos.contract;

import jakarta.validation.constraints.NotBlank;

// Creating/updating a contract
public class ContractCreateDTO {
    @NotBlank
    private String title;

    private String content; // optional for initial draft
}
