package com.mkr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mkr.dto.JobSeekerDashboardResponse;
import com.mkr.service.JobSeekerDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job-seeker")
@RequiredArgsConstructor
public class JobSeekerDashboardController {

    private final JobSeekerDashboardService jobSeekerDashboardService;

    // Returns dashboard information for the currently authenticated job seeker.
    @GetMapping("/dashboard")
    public ResponseEntity<JobSeekerDashboardResponse> getDashboard(
            Authentication authentication) {

        JobSeekerDashboardResponse response =
                jobSeekerDashboardService.getJobSeekerDashboard(
                        authentication.getName());

        return ResponseEntity.ok(response);
    }
}