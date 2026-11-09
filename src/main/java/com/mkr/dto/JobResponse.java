
package com.mkr.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.mkr.enums.JobStatus;
import com.mkr.enums.JobType;
import com.mkr.enums.WorkMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String description;
    private String location;
    private BigDecimal salary;
    private Integer experienceYears;
    private JobType jobType;
    private WorkMode workMode;
    private JobStatus status;
    private LocalDateTime createdAt;
    private Long recruiterId;
    private List<String> requiredSkills;
}
