package com.mkr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mkr.dto.RecruiterDashboardResponse;
import com.mkr.service.RecruiterDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterDashboardController {

    private final RecruiterDashboardService recruiterDashboardService;

    // Returns dashboard information for the currently authenticated recruiter.
    @GetMapping("/dashboard")
    public ResponseEntity<RecruiterDashboardResponse> getDashboard(
            Authentication authentication) {

        RecruiterDashboardResponse response =
                recruiterDashboardService.getRecruiterDashboard(authentication.getName());

        return ResponseEntity.ok(response);
    }
}