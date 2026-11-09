//

package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mkr.dto.ApplicationResponse;
import com.mkr.enums.ApplicationStatus;
import com.mkr.enums.JobStatus;
import com.mkr.enums.RecruiterStatus;
import com.mkr.enums.Role;
import com.mkr.model.Job;
import com.mkr.model.JobApplication;
import com.mkr.model.RecruiterProfile;
import com.mkr.model.User;
import com.mkr.repository.JobApplicationRepository;
import com.mkr.repository.JobRepository;
import com.mkr.repository.RecruiterProfileRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    // Find the currently authenticated user
    private User getUserByEmail(String email) {

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    // Check whether user is an active Job Seeker
    private User getActiveJobSeeker(String email) {

        User user = getUserByEmail(email);

        if (user.getRole() != Role.JOB_SEEKER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only job seekers can perform this operation");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is inactive");
        }

        return user;
    }

    // Check whether user is an approved and active Recruiter
    private User getApprovedRecruiter(String email) {

        User recruiter = getUserByEmail(email);

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can perform this operation");
        }

        if (!recruiter.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Recruiter account is inactive");
        }

        RecruiterProfile profile = recruiterProfileRepository
                .findByUserId(recruiter.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Recruiter profile not found"));

        if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Recruiter is not approved");
        }

        return recruiter;
    }

    // 1. Job Seeker applies for a job
    @Override
    @Transactional
    public ApplicationResponse applyForJob(Long jobId, String email) {

        User jobSeeker = getActiveJobSeeker(email);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job not found"));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot apply for a closed job");
        }

        if (applicationRepository.existsByJobIdAndJobSeekerId(
                jobId, jobSeeker.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already applied for this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(ApplicationStatus.PENDING);

        JobApplication savedApplication =
                applicationRepository.save(application);

        return mapToApplicationResponse(savedApplication);
    }

    // 2. Job Seeker views their own applications
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(String email) {

        User jobSeeker = getActiveJobSeeker(email);

        return applicationRepository
                .findByJobSeekerId(jobSeeker.getId())
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    // 3. View application by ID
    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(
            Long applicationId, String email) {

        User user = getUserByEmail(email);

        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is inactive");
        }

        JobApplication application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));

        // Job Seeker can view only their own application
        if (user.getRole() == Role.JOB_SEEKER) {

            if (!application.getJobSeeker().getId().equals(user.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot view another job seeker's application");
            }

        }
        // Recruiter can view applications for their own jobs
        else if (user.getRole() == Role.RECRUITER) {

            getApprovedRecruiter(email);

            if (application.getJob().getRecruiter() == null ||
                    !application.getJob().getRecruiter().getId()
                            .equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot view another recruiter's application");
            }

        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view this application");
        }

        return mapToApplicationResponse(application);
    }

    // 4. Job Seeker withdraws their application
    @Override
    @Transactional
    public ApplicationResponse withdrawApplication(
            Long applicationId, String email) {

        User jobSeeker = getActiveJobSeeker(email);

        JobApplication application = applicationRepository
                .findByIdAndJobSeekerId(
                        applicationId, jobSeeker.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found for this job seeker"));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending applications can be withdrawn");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        JobApplication updatedApplication =
                applicationRepository.save(application);

        return mapToApplicationResponse(updatedApplication);
    }

    // 5. Recruiter views applications for their own job
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJob(
            Long jobId, String email) {

        User recruiter = getApprovedRecruiter(email);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job not found"));

        if (job.getRecruiter() == null ||
                !job.getRecruiter().getId().equals(recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view applications for another recruiter's job");
        }

        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    // 6. Recruiter updates application status
    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(
            Long applicationId,
            String email,
            ApplicationStatus status) {

        User recruiter = getApprovedRecruiter(email);

        JobApplication application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));

        if (application.getJob().getRecruiter() == null ||
                !application.getJob().getRecruiter().getId()
                        .equals(recruiter.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot update applications for another recruiter's job");
        }

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application status is required");
        }

        if (status == ApplicationStatus.WITHDRAWN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recruiters cannot set application status to WITHDRAWN");
        }

        application.setStatus(status);

        JobApplication updatedApplication =
                applicationRepository.save(application);

        return mapToApplicationResponse(updatedApplication);
    }

    // 7. Convert entity into response DTO
    private ApplicationResponse mapToApplicationResponse(
            JobApplication application) {

        return new ApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompanyName(),
                application.getJobSeeker().getId(),
                application.getJobSeeker().getFullName(),
                application.getJobSeeker().getEmail(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}





//             before JWT 
//package com.mkr.serviceImpl;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//import com.mkr.dto.ApplicationResponse;
//import com.mkr.enums.ApplicationStatus;
//import com.mkr.enums.JobStatus;
//import com.mkr.enums.RecruiterStatus;
//import com.mkr.enums.Role;
//import com.mkr.model.Job;
//import com.mkr.model.JobApplication;
//import com.mkr.model.RecruiterProfile;
//import com.mkr.model.User;
//import com.mkr.repository.JobApplicationRepository;
//import com.mkr.repository.JobRepository;
//import com.mkr.repository.RecruiterProfileRepository;
//import com.mkr.repository.UserRepository;
//import com.mkr.service.JobApplicationService;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class JobApplicationServiceImpl implements JobApplicationService {
//
//    private final JobApplicationRepository applicationRepository;
//    private final JobRepository jobRepository;
//    private final UserRepository userRepository;
//    private final RecruiterProfileRepository recruiterProfileRepository;
//
//    // 1. Job Seeker applies for a job
//    @Override
//    @Transactional
//    public ApplicationResponse applyForJob(Long jobId, Long jobSeekerId) {
//
//        Job job = jobRepository.findById(jobId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Job not found"));
//
//        User jobSeeker = userRepository.findById(jobSeekerId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Job seeker not found"));
//
//        if (jobSeeker.getRole() != Role.JOB_SEEKER) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Only job seekers can apply for jobs");
//        }
//
//        if (!jobSeeker.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Your account is inactive");
//        }
//
//        if (job.getStatus() != JobStatus.OPEN) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Cannot apply for a closed job");
//        }
//
//        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, jobSeekerId)) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT,
//                    "You have already applied for this job");
//        }
//
//        JobApplication application = new JobApplication();
//        application.setJob(job);
//        application.setJobSeeker(jobSeeker);
//        application.setStatus(ApplicationStatus.PENDING);
//
//        JobApplication savedApplication =
//                applicationRepository.save(application);
//
//        return mapToApplicationResponse(savedApplication);
//    }
//
//    // 2. Job Seeker views their own applications
//    @Override
//    @Transactional(readOnly = true)
//    public List<ApplicationResponse> getMyApplications(Long jobSeekerId) {
//
//        User jobSeeker = userRepository.findById(jobSeekerId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Job seeker not found"));
//
//        if (jobSeeker.getRole() != Role.JOB_SEEKER) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Only job seekers can view their applications");
//        }
//
//        if (!jobSeeker.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Your account is inactive");
//        }
//
//        return applicationRepository.findByJobSeekerId(jobSeekerId)
//                .stream()
//                .map(this::mapToApplicationResponse)
//                .toList();
//    }
//
//    // 3. View application by ID with authorization
//    @Override
//    @Transactional(readOnly = true)
//    public ApplicationResponse getApplicationById(
//            Long applicationId, Long userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!user.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Your account is inactive");
//        }
//
//        JobApplication application = applicationRepository.findById(applicationId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Application not found"));
//
//        // Job Seeker can view only their own application
//        if (user.getRole() == Role.JOB_SEEKER) {
//
//            if (!application.getJobSeeker().getId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.FORBIDDEN,
//                        "You cannot view another job seeker's application");
//            }
//
//        }
//        // Recruiter can view applications for their own jobs
//        else if (user.getRole() == Role.RECRUITER) {
//
//            if (!application.getJob().getRecruiter().getId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.FORBIDDEN,
//                        "You cannot view another recruiter's application");
//            }
//
//            RecruiterProfile profile = recruiterProfileRepository
//                    .findByUserId(userId)
//                    .orElseThrow(() -> new ResponseStatusException(
//                            HttpStatus.FORBIDDEN,
//                            "Recruiter profile not found"));
//
//            if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
//                throw new ResponseStatusException(
//                        HttpStatus.FORBIDDEN,
//                        "Recruiter is not approved");
//            }
//
//        } else {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You are not authorized to view this application");
//        }
//
//        return mapToApplicationResponse(application);
//    }
//
//    // 4. Job Seeker withdraws their application
//    @Override
//    @Transactional
//    public ApplicationResponse withdrawApplication(
//            Long applicationId, Long jobSeekerId) {
//
//        User jobSeeker = userRepository.findById(jobSeekerId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Job seeker not found"));
//
//        if (jobSeeker.getRole() != Role.JOB_SEEKER) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Only job seekers can withdraw applications");
//        }
//
//        if (!jobSeeker.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Your account is inactive");
//        }
//
//        JobApplication application = applicationRepository
//                .findByIdAndJobSeekerId(applicationId, jobSeekerId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Application not found for this job seeker"));
//
//        if (application.getStatus() != ApplicationStatus.PENDING) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Only pending applications can be withdrawn");
//        }
//
//        application.setStatus(ApplicationStatus.WITHDRAWN);
//
//        JobApplication updatedApplication =
//                applicationRepository.save(application);
//
//        return mapToApplicationResponse(updatedApplication);
//    }
//
//    // 5. Recruiter views applications for their own job
//    @Override
//    @Transactional(readOnly = true)
//    public List<ApplicationResponse> getApplicationsByJob(
//            Long jobId, Long recruiterId) {
//
//        Job job = jobRepository.findById(jobId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Job not found"));
//
//        User recruiter = userRepository.findById(recruiterId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Recruiter not found"));
//
//        if (recruiter.getRole() != Role.RECRUITER) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Only recruiters can view applicants");
//        }
//
//        if (!recruiter.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Recruiter account is inactive");
//        }
//
//        if (!job.getRecruiter().getId().equals(recruiterId)) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You cannot view applications for another recruiter's job");
//        }
//
//        RecruiterProfile profile = recruiterProfileRepository
//                .findByUserId(recruiterId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.FORBIDDEN,
//                        "Recruiter profile not found"));
//
//        if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Recruiter is not approved");
//        }
//
//        return applicationRepository.findByJobId(jobId)
//                .stream()
//                .map(this::mapToApplicationResponse)
//                .toList();
//    }
//
//    // 6. Recruiter updates application status
//    @Override
//    @Transactional
//    public ApplicationResponse updateApplicationStatus(
//            Long applicationId,
//            Long recruiterId,
//            ApplicationStatus status) {
//
//        User recruiter = userRepository.findById(recruiterId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Recruiter not found"));
//
//        if (recruiter.getRole() != Role.RECRUITER) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Only recruiters can update application status");
//        }
//
//        if (!recruiter.isActive()) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Recruiter account is inactive");
//        }
//
//        RecruiterProfile profile = recruiterProfileRepository
//                .findByUserId(recruiterId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.FORBIDDEN,
//                        "Recruiter profile not found"));
//
//        if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "Recruiter is not approved");
//        }
//
//        JobApplication application = applicationRepository.findById(applicationId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Application not found"));
//
//        if (!application.getJob().getRecruiter().getId().equals(recruiterId)) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You cannot update applications for another recruiter's job");
//        }
//
//        if (status == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Application status is required");
//        }
//
//        if (status == ApplicationStatus.WITHDRAWN) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Recruiters cannot set application status to WITHDRAWN");
//        }
//
//        application.setStatus(status);
//
//        JobApplication updatedApplication =
//                applicationRepository.save(application);
//
//        return mapToApplicationResponse(updatedApplication);
//    }
//
//    // 7. Convert entity into response DTO
//    private ApplicationResponse mapToApplicationResponse(
//            JobApplication application) {
//
//        return new ApplicationResponse(
//                application.getId(),
//                application.getJob().getId(),
//                application.getJob().getTitle(),
//                application.getJob().getCompanyName(),
//                application.getJobSeeker().getId(),
//                application.getJobSeeker().getFullName(),
//                application.getJobSeeker().getEmail(),
//                application.getStatus(),
//                application.getAppliedAt()
//        );
//    }
//}
