
package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mkr.dto.AdminDashboardResponse;
import com.mkr.dto.RecentApplicationResponse;
import com.mkr.dto.RecentJobResponse;
import com.mkr.enums.JobStatus;
import com.mkr.enums.RecruiterStatus;
import com.mkr.enums.Role;
import com.mkr.repository.JobApplicationRepository;
import com.mkr.repository.JobRepository;
import com.mkr.repository.RecruiterProfileRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.AdminService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

 // Fetches all dashboard statistics and recent activity for the admin.
    @Override
    @Transactional
    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();

        long totalJobSeekers = userRepository.countByRole(Role.JOB_SEEKER);

        long totalRecruiters = userRepository.countByRole(Role.RECRUITER);

        long totalJobs = jobRepository.count();

        long openJobs = jobRepository.countByStatus(JobStatus.OPEN);

        long closedJobs = jobRepository.countByStatus(JobStatus.CLOSED);

        long draftJobs = jobRepository.countByStatus(JobStatus.DRAFT);

        long totalApplications = jobApplicationRepository.count();

        long pendingRecruiters = recruiterProfileRepository
                .countByRecruiterStatus(RecruiterStatus.PENDING);

        List<RecentJobResponse> recentJobs = getRecentJobs();

        List<RecentApplicationResponse> recentApplications =
                getRecentApplications();

        return new AdminDashboardResponse(
                totalUsers,
                totalJobSeekers,
                totalRecruiters,
                totalJobs,
                openJobs,
                closedJobs,
                draftJobs,
                totalApplications,
                pendingRecruiters,
                recentJobs,
                recentApplications
        );
    }

    // Fetches the latest 5 jobs and converts them into response DTOs.
    private List<RecentJobResponse> getRecentJobs() {

        return jobRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(job -> new RecentJobResponse(
                        job.getId(),
                        job.getTitle(),
                        job.getCompanyName(),
                        job.getLocation(),
                        job.getJobType(),
                        job.getWorkMode(),
                        job.getStatus(),
                        job.getCreatedAt(),
                        job.getRecruiter().getId()
                ))
                .toList();
    }

    // Fetches the latest 5 applications and converts them into response DTOs.
    private List<RecentApplicationResponse> getRecentApplications() {

        return jobApplicationRepository.findTop5ByOrderByAppliedAtDesc()
                .stream()
                .map(application -> new RecentApplicationResponse(
                        application.getId(),
                        application.getJob().getId(),
                        application.getJob().getTitle(),
                        application.getJobSeeker().getId(),
                        application.getJobSeeker().getFullName(),
                        application.getJobSeeker().getEmail(),
                        application.getStatus(),
                        application.getAppliedAt()
                ))
                .toList();
    }
}
