package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mkr.dto.RecentRecruiterApplicationResponse;
import com.mkr.dto.RecruiterDashboardResponse;
import com.mkr.enums.ApplicationStatus;
import com.mkr.enums.JobStatus;
import com.mkr.enums.Role;
import com.mkr.model.JobApplication;
import com.mkr.model.User;
import com.mkr.repository.JobApplicationRepository;
import com.mkr.repository.JobRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.RecruiterDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruiterDashboardServiceImpl implements RecruiterDashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    // Retrieves dashboard statistics and recent applications for the logged-in recruiter.
    @Override
    @Transactional(readOnly = true)
    public RecruiterDashboardResponse getRecruiterDashboard(String email) {

        User recruiter = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recruiter not found"));

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only recruiters can access this dashboard");
        }

        Long recruiterId = recruiter.getId();

        long totalJobs = jobRepository.countByRecruiterId(recruiterId);
        long openJobs = jobRepository.countByRecruiterIdAndStatus(recruiterId, JobStatus.OPEN);
        long closedJobs = jobRepository.countByRecruiterIdAndStatus(recruiterId, JobStatus.CLOSED);
        long draftJobs = jobRepository.countByRecruiterIdAndStatus(recruiterId, JobStatus.DRAFT);

        long totalApplications = jobApplicationRepository.countByJobRecruiterId(recruiterId);

        long pendingApplications = jobApplicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId, ApplicationStatus.PENDING);

        long shortlistedApplications = jobApplicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId, ApplicationStatus.SHORTLISTED);

        long rejectedApplications = jobApplicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId, ApplicationStatus.REJECTED);

        long acceptedApplications = jobApplicationRepository.countByJobRecruiterIdAndStatus(
                recruiterId, ApplicationStatus.ACCEPTED);

        List<RecentRecruiterApplicationResponse> recentApplications =
                jobApplicationRepository
                        .findTop5ByJobRecruiter_IdOrderByAppliedAtDesc(recruiterId)
                        .stream()
                        .map(this::mapToRecentApplicationResponse)
                        .toList();

        return new RecruiterDashboardResponse(
                totalJobs,
                openJobs,
                closedJobs,
                draftJobs,
                totalApplications,
                pendingApplications,
                shortlistedApplications,
                rejectedApplications,
                acceptedApplications,
                recentApplications
        );
    }

    // Converts an application entity into the recruiter dashboard response DTO.
    private RecentRecruiterApplicationResponse mapToRecentApplicationResponse(
            JobApplication application) {

        return new RecentRecruiterApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJobSeeker().getId(),
                application.getJobSeeker().getFullName(),
                application.getJobSeeker().getEmail(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}