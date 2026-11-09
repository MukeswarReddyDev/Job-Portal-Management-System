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
public class RecentRecruiterApplicationResponse {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long jobSeekerId;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}