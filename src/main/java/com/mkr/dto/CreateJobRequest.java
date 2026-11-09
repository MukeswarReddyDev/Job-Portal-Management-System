
package com.mkr.dto;

import java.math.BigDecimal;
import java.util.List;

import com.mkr.enums.JobType;
import com.mkr.enums.WorkMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(max = 100)
    private String location;

    @DecimalMin(value = "0.0",
            message = "Salary cannot be negative")
    private BigDecimal salary;

    @Min(value = 0,
            message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @NotEmpty(message = "At least one required skill is needed")
    private List<@NotNull @Positive Long> requiredSkillIds;
}
