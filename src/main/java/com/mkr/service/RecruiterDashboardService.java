package com.mkr.service;

import com.mkr.dto.RecruiterDashboardResponse;

public interface RecruiterDashboardService {

    RecruiterDashboardResponse getRecruiterDashboard(String email);
}