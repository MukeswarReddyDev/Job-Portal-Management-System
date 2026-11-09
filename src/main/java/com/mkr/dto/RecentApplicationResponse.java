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
public class RecentApplicationResponse {

    // Unique ID of the application.
    private Long applicationId;

    // ID of the job for which the candidate applied.
    private Long jobId;

    // Title of the applied job.
    private String jobTitle;

    // ID of the job seeker who submitted the application.
    private Long jobSeekerId;

    // Full name of the job seeker.
    private String jobSeekerName;

    // Email of the job seeker.
    private String jobSeekerEmail;

    // Current application status: PENDING, SHORTLISTED, REJECTED, ACCEPTED or WITHDRAWN.
    private ApplicationStatus status;

    // Date and time when the application was submitted.
    private LocalDateTime appliedAt;
}