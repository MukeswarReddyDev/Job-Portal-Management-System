package com.mkr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerDashboardResponse {

    private long totalApplications;
    private long pendingApplications;
    private long shortlistedApplications;
    private long rejectedApplications;
    private long acceptedApplications;
    private long withdrawnApplications;

    private List<RecentJobSeekerApplicationResponse> recentApplications;
}