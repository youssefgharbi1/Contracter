package com.project.contracter.dtos.contractTemplate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractTemplateCreateDTO {
    @NotBlank
    private String name;
    private String description;
    private String content;
}
