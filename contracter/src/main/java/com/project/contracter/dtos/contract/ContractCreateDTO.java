package com.project.contracter.dtos.contract;

import com.project.contracter.enums.ContractCategory;
import com.project.contracter.enums.ContractStatus;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ContractCreateDTO {
    @NotBlank
    private String title;
    private ContractCategory category;
    private String description;
    private final ContractStatus status = ContractStatus.DRAFT;

}
