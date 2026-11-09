package com.mkr.service;

import com.mkr.dto.JobSeekerDashboardResponse;

public interface JobSeekerDashboardService {

    JobSeekerDashboardResponse getJobSeekerDashboard(String email);
}