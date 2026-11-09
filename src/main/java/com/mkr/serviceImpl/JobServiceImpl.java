
package com.mkr.serviceImpl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mkr.dto.CreateJobRequest;
import com.mkr.dto.JobFilterRequest;
import com.mkr.dto.JobResponse;
import com.mkr.dto.UpdateJobRequest;
import com.mkr.enums.JobStatus;
import com.mkr.enums.RecruiterStatus;
import com.mkr.enums.Role;
import com.mkr.exception.BadRequestException;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.Job;
import com.mkr.model.RecruiterProfile;
import com.mkr.model.Skill;
import com.mkr.model.User;
import com.mkr.repository.JobRepository;
import com.mkr.repository.RecruiterProfileRepository;
import com.mkr.repository.SkillRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.JobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

	private final JobRepository jobRepository;
	private final UserRepository userRepository;
	private final RecruiterProfileRepository recruiterProfileRepository;
	private final SkillRepository skillRepository;

	// 1. Create a job
	@Override
	@Transactional
	public JobResponse createJob(Long recruiterId, CreateJobRequest request) {

		// Find recruiter
		User recruiter = userRepository.findById(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + recruiterId));

		// Check recruiter role
		if (recruiter.getRole() != Role.RECRUITER) {
			throw new BadRequestException("Only recruiters can create job postings");
		}

		// Check recruiter active status
		if (!recruiter.isActive()) {
			throw new BadRequestException("Inactive recruiter cannot create jobs");
		}

		// Find recruiter profile
		RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

		// Check recruiter approval
		if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
			throw new BadRequestException("Only approved recruiters can create jobs");
		}

		// Validate skill IDs
		Set<Long> skillIds = new LinkedHashSet<>(request.getRequiredSkillIds());

		if (skillIds.size() != request.getRequiredSkillIds().size()) {
			throw new BadRequestException("Duplicate skill IDs are not allowed");
		}

		List<Skill> skills = skillRepository.findAllById(skillIds.stream().toList());

		if (skills.size() != skillIds.size()) {
			throw new BadRequestException("One or more skill IDs are invalid");
		}

		// Create job
		Job job = new Job();

		job.setTitle(request.getTitle().trim());
		job.setCompanyName(profile.getCompanyName());
		job.setDescription(request.getDescription().trim());
		job.setLocation(request.getLocation().trim());
		job.setSalary(request.getSalary());
		job.setExperienceYears(request.getExperienceYears());
		job.setJobType(request.getJobType());
		job.setWorkMode(request.getWorkMode());

		job.setStatus(JobStatus.OPEN);
		job.setRecruiter(recruiter);

		// Associate required skills
		job.setRequiredSkills(new LinkedHashSet<>(skills));

		// Save job
		Job savedJob = jobRepository.save(job);

		return mapToJobResponse(savedJob);
	}

	// 2. Get all jobs
	@Override
	@Transactional(readOnly = true)
	public List<JobResponse> getAllJobs() {

		return jobRepository.findAll().stream().map(this::mapToJobResponse).collect(Collectors.toList());
	}

	// 3. Convert Job entity into JobResponse DTO
	private JobResponse mapToJobResponse(Job job) {

		List<String> skillNames = job.getRequiredSkills().stream().map(Skill::getSkillName).sorted()
				.collect(Collectors.toList());

		return new JobResponse(job.getId(), job.getTitle(), job.getCompanyName(), job.getDescription(),
				job.getLocation(), job.getSalary(), job.getExperienceYears(), job.getJobType(), job.getWorkMode(),
				job.getStatus(), job.getCreatedAt(), job.getRecruiter().getId(), skillNames);
	}

	///// Updating Job Details

	@Override
	@Transactional
	public JobResponse updateJob(Long jobId, Long recruiterId, UpdateJobRequest request) {

		// 1. Find job
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

		// 2. Find recruiter
		User recruiter = userRepository.findById(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + recruiterId));

		// 3. Check recruiter role
		if (recruiter.getRole() != Role.RECRUITER) {
			throw new BadRequestException("Only recruiters can update jobs");
		}

		// 4. Check recruiter active status
		if (!recruiter.isActive()) {
			throw new BadRequestException("Inactive recruiter cannot update jobs");
		}

		// 5. Check job ownership
		if (!job.getRecruiter().getId().equals(recruiterId)) {
			throw new BadRequestException("You can only update your own job postings");
		}

		// 6. Find recruiter profile
		RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

		// 7. Check recruiter approval
		if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
			throw new BadRequestException("Only approved recruiters can update jobs");
		}

		// 8. Validate skill IDs
		Set<Long> skillIds = new LinkedHashSet<>(request.getRequiredSkillIds());

		if (skillIds.size() != request.getRequiredSkillIds().size()) {
			throw new BadRequestException("Duplicate skill IDs are not allowed");
		}

		List<Skill> skills = skillRepository.findAllById(skillIds);

		if (skills.size() != skillIds.size()) {
			throw new BadRequestException("One or more skill IDs are invalid");
		}

		// 9. Update job details
		job.setTitle(request.getTitle().trim());
		job.setDescription(request.getDescription().trim());
		job.setLocation(request.getLocation().trim());
		job.setSalary(request.getSalary());
		job.setExperienceYears(request.getExperienceYears());
		job.setJobType(request.getJobType());
		job.setWorkMode(request.getWorkMode());

		// 10. Replace existing required skills
		job.setRequiredSkills(new LinkedHashSet<>(skills));

		// 11. Save updated job
		Job updatedJob = jobRepository.save(job);

		return mapToJobResponse(updatedJob);
	}

	@Override
	@Transactional
	public void deleteJob(Long jobId, Long recruiterId) {

		// 1. Find job
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

		// 2. Find recruiter
		User recruiter = userRepository.findById(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + recruiterId));

		// 3. Check recruiter role
		if (recruiter.getRole() != Role.RECRUITER) {
			throw new BadRequestException("Only recruiters can delete jobs");
		}

		// 4. Check active status
		if (!recruiter.isActive()) {
			throw new BadRequestException("Inactive recruiter cannot delete jobs");
		}

		// 5. Check job ownership
		if (!job.getRecruiter().getId().equals(recruiterId)) {
			throw new BadRequestException("You can only delete your own job postings");
		}

		// 6. Check recruiter approval
		RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

		if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
			throw new BadRequestException("Only approved recruiters can delete jobs");
		}

		// 7. Prevent deletion if applications exist
		if (!job.getApplications().isEmpty()) {
			throw new BadRequestException("Cannot delete a job that has applications. Close the job instead.");
		}

		// 8. Delete job
		jobRepository.delete(job);
	}

	// Close a Job Application

	@Override
	@Transactional
	public void closeJob(Long jobId, Long recruiterId) {

		// 1. Find job
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

		// 2. Validate recruiter
		validateRecruiterOwnership(job, recruiterId);

		// 3. Check current status
		if (job.getStatus() != JobStatus.OPEN) {
			throw new BadRequestException("Only open jobs can be closed");
		}

		// 4. Change status
		job.setStatus(JobStatus.CLOSED);

		jobRepository.save(job);
	}

// Reopen a Job 

	@Override
	@Transactional
	public void reopenJob(Long jobId, Long recruiterId) {

		// 1. Find job
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

		// 2. Validate recruiter
		validateRecruiterOwnership(job, recruiterId);

		// 3. Check current status
		if (job.getStatus() != JobStatus.CLOSED) {
			throw new BadRequestException("Only closed jobs can be reopened");
		}

		// 4. Change status
		job.setStatus(JobStatus.OPEN);

		jobRepository.save(job);
	}

// Common recruiter ownership validation
	private void validateRecruiterOwnership(Job job, Long recruiterId) {

		User recruiter = userRepository.findById(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + recruiterId));

		if (recruiter.getRole() != Role.RECRUITER) {
			throw new BadRequestException("Only recruiters can manage jobs");
		}

		if (!recruiter.isActive()) {
			throw new BadRequestException("Inactive recruiter cannot manage jobs");
		}

		if (!job.getRecruiter().getId().equals(recruiterId)) {
			throw new BadRequestException("You can only manage your own job postings");
		}

		RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiterId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

		if (profile.getRecruiterStatus() != RecruiterStatus.APPROVED) {
			throw new BadRequestException("Only approved recruiters can manage jobs");
		}
	}
	
	
	
	// Get all open jobs 
	
	@Override
	@Transactional(readOnly = true)
	public List<JobResponse> getOpenJobs() {

	    return jobRepository.findByStatus(JobStatus.OPEN)
	            .stream()
	            .map(this::mapToJobResponse)
	            .toList();
	}
	
	// Get Jobs By Keywords 
	
	@Override
	@Transactional(readOnly = true)
	public List<JobResponse> searchJobs(String keyword) {

	    if (keyword == null || keyword.isBlank()) {
	        throw new RuntimeException("Search keyword cannot be empty");
	    }

	    return jobRepository.searchOpenJobs(JobStatus.OPEN, keyword.trim())
	            .stream()
	            .map(this::mapToJobResponse)
	            .toList();
	}

	
	// Filter Jobs based all the filters like Keywords, type, experience, status, all other things 
	
	@Override
	@Transactional(readOnly = true)
	public List<JobResponse> filterJobs(JobFilterRequest request) {

	    if (request.getMinExperience() != null
	            && request.getMinExperience() < 0) {
	        throw new RuntimeException(
	                "Minimum experience cannot be negative");
	    }

	    if (request.getMaxExperience() != null
	            && request.getMaxExperience() < 0) {
	        throw new RuntimeException(
	                "Maximum experience cannot be negative");
	    }

	    if (request.getMinExperience() != null
	            && request.getMaxExperience() != null
	            && request.getMinExperience() > request.getMaxExperience()) {
	        throw new RuntimeException(
	                "Minimum experience cannot exceed maximum experience");
	    }

	    if (request.getMinSalary() != null
	            && request.getMinSalary().compareTo(BigDecimal.ZERO) < 0) {
	        throw new RuntimeException(
	                "Minimum salary cannot be negative");
	    }

	    if (request.getMaxSalary() != null
	            && request.getMaxSalary().compareTo(BigDecimal.ZERO) < 0) {
	        throw new RuntimeException(
	                "Maximum salary cannot be negative");
	    }

	    if (request.getMinSalary() != null
	            && request.getMaxSalary() != null
	            && request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {
	        throw new RuntimeException(
	                "Minimum salary cannot exceed maximum salary");
	    }

	    String keyword = normalize(request.getKeyword());
	    String location = normalize(request.getLocation());

	    return jobRepository.filterJobs(
	            JobStatus.OPEN,
	            keyword,
	            location,
	            request.getJobType(),
	            request.getWorkMode(),
	            request.getMinExperience(),
	            request.getMaxExperience(),
	            request.getMinSalary(),
	            request.getMaxSalary()
	    )
	    .stream()
	    .map(this::mapToJobResponse)
	    .toList();
	}

	private String normalize(String value) {
	    return value == null || value.isBlank()
	            ? null
	            : value.trim();
	}
}
