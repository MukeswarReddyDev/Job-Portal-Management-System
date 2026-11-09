package com.mkr.dto;

import java.time.LocalDateTime;

import com.mkr.enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;

    private Long jobId;

    private String jobTitle;

    private String companyName;

    private Long jobSeekerId;

    private String jobSeekerName;

    private String jobSeekerEmail;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;
}
