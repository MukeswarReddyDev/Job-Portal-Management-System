package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mkr.dto.JobSeekerDashboardResponse;
import com.mkr.dto.RecentJobSeekerApplicationResponse;
import com.mkr.enums.ApplicationStatus;
import com.mkr.enums.Role;
import com.mkr.model.JobApplication;
import com.mkr.model.User;
import com.mkr.repository.JobApplicationRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.JobSeekerDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobSeekerDashboardServiceImpl implements JobSeekerDashboardService {

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;

    // Retrieves application statistics and recent applications for the logged-in job seeker.
    @Override
    @Transactional(readOnly = true)
    public JobSeekerDashboardResponse getJobSeekerDashboard(String email) {

        User jobSeeker = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job seeker not found"));

        if (jobSeeker.getRole() != Role.JOB_SEEKER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only job seekers can access this dashboard");
        }

        Long jobSeekerId = jobSeeker.getId();

        long totalApplications =
                jobApplicationRepository.countByJobSeekerId(jobSeekerId);

        long pendingApplications =
                jobApplicationRepository.countByJobSeekerIdAndStatus(
                        jobSeekerId, ApplicationStatus.PENDING);

        long shortlistedApplications =
                jobApplicationRepository.countByJobSeekerIdAndStatus(
                        jobSeekerId, ApplicationStatus.SHORTLISTED);

        long rejectedApplications =
                jobApplicationRepository.countByJobSeekerIdAndStatus(
                        jobSeekerId, ApplicationStatus.REJECTED);

        long acceptedApplications =
                jobApplicationRepository.countByJobSeekerIdAndStatus(
                        jobSeekerId, ApplicationStatus.ACCEPTED);

        long withdrawnApplications =
                jobApplicationRepository.countByJobSeekerIdAndStatus(
                        jobSeekerId, ApplicationStatus.WITHDRAWN);

        List<RecentJobSeekerApplicationResponse> recentApplications =
                jobApplicationRepository
                        .findTop5ByJobSeekerIdOrderByAppliedAtDesc(jobSeekerId)
                        .stream()
                        .map(this::mapToRecentApplicationResponse)
                        .toList();

        return new JobSeekerDashboardResponse(
                totalApplications,
                pendingApplications,
                shortlistedApplications,
                rejectedApplications,
                acceptedApplications,
                withdrawnApplications,
                recentApplications
        );
    }

    // Converts an application entity into the job seeker dashboard response DTO.
    private RecentJobSeekerApplicationResponse mapToRecentApplicationResponse(
            JobApplication application) {

        return new RecentJobSeekerApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompanyName(),
                application.getJob().getLocation(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}