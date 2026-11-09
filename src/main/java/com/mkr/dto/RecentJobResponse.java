package com.mkr.dto;

import java.time.LocalDateTime;

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
public class RecentJobResponse {

    // Unique ID of the job.
    private Long jobId;

    // Job title displayed on the Admin Dashboard.
    private String title;

    // Company that posted the job.
    private String companyName;

    // Location where the job is available.
    private String location;

    // Employment type: FULL_TIME, PART_TIME, INTERNSHIP or CONTRACT.
    private JobType jobType;

    // Work arrangement: ONSITE, REMOTE or HYBRID.
    private WorkMode workMode;

    // Current job status: OPEN, CLOSED or DRAFT.
    private JobStatus status;

    // Date and time when the job was created.
    private LocalDateTime createdAt;

    // ID of the recruiter who posted the job.
    private Long recruiterId;
}