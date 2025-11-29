package com.project.contracter.dtos.contractTemplate;

// Creating template
public class ContractTemplateCreateDTO {
    @NotBlank
    private String name;
    private String description;
    private String content;
}
