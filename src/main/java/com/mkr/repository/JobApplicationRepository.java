package com.mkr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mkr.enums.ApplicationStatus;
import com.mkr.model.JobApplication;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

	// Check whether a job seeker has already applied
	boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

	// Get all applications submitted by a job seeker
	List<JobApplication> findByJobSeekerId(Long jobSeekerId);

	// Get all applications received for a particular job
	List<JobApplication> findByJobId(Long jobId);

	// Find an application belonging to a particular job seeker
	Optional<JobApplication> findByIdAndJobSeekerId(Long applicationId, Long jobSeekerId);

	// This gives us the total number of job applications.
	long count();

	// Retrieves the latest 5 job applications submitted by job seekers.
	// OrderByAppliedAtDesc sorts applications by applied date in descending order.
	// Desc means the most recently submitted applications appear first.
	// Used by the Admin Dashboard to display recent application activity.

	List<JobApplication> findTop5ByOrderByAppliedAtDesc();
	
	
	// Counts all applications received for jobs posted by a recruiter.
	long countByJobRecruiterId(Long recruiterId);

	// Counts applications received by a recruiter based on application status.
	long countByJobRecruiterIdAndStatus(Long recruiterId, ApplicationStatus status);

	// Retrieves the latest 5 applications received for a recruiter's jobs.
	List<JobApplication> findTop5ByJobRecruiter_IdOrderByAppliedAtDesc(Long recruiterId);

	// Counts all applications submitted by a particular job seeker.
	long countByJobSeekerId(Long jobSeekerId);

	// Counts a job seeker's applications based on application status.
	long countByJobSeekerIdAndStatus(Long jobSeekerId, ApplicationStatus status);

	// Retrieves the latest 5 applications submitted by a job seeker.
	List<JobApplication> findTop5ByJobSeekerIdOrderByAppliedAtDesc(Long jobSeekerId);
	
	
	
	
	
	
	
	
	
	
	
}