package com.mkr.dto;

import java.math.BigDecimal;

import com.mkr.enums.JobType;
import com.mkr.enums.WorkMode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobFilterRequest {

    private String keyword;

    private String location;

    private JobType jobType;

    private WorkMode workMode;

    private Integer minExperience;

    private Integer maxExperience;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;
}