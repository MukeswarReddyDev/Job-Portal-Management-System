
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
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalJobSeekers;

    private long totalRecruiters;

    private long totalJobs;

    private long openJobs;

    private long closedJobs;

    private long draftJobs;

    private long totalApplications;

    private long pendingRecruiters;
    
 // Stores the latest 5 jobs posted.
    private List<RecentJobResponse> recentJobs;

    // Stores the latest 5 applications submitted.
    private List<RecentApplicationResponse> recentApplications;

}
