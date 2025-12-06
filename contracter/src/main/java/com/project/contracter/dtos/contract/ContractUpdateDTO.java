package com.project.contracter.dtos.contract;

import com.project.contracter.enums.ContractCategory;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class ContractUpdateDTO {

    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;  // Optional


    private ContractCategory category;  // Optional

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;  // Optional

    private Instant updatedAt;
}