package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mkr.dto.SkillResponse;
import com.mkr.enums.Role;
import com.mkr.exception.BadRequestException;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.Job;
import com.mkr.model.Skill;
import com.mkr.model.User;
import com.mkr.repository.JobRepository;
import com.mkr.repository.SkillRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.SkillAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillAssignmentServiceImpl implements SkillAssignmentService {

	private final UserRepository userRepository;
	private final JobRepository jobRepository;
	private final SkillRepository skillRepository;

	// Add skill to job seeker
	@Override
	public SkillResponse addSkillToUser(Long userId, Long skillId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		Skill skill = skillRepository.findById(skillId)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

		if (user.getRole() != Role.JOB_SEEKER) {
			throw new BadRequestException("Skills can only be assigned to job seekers");
		}

		if (!user.isActive()) {
			throw new BadRequestException("Cannot update an inactive user");
		}

		if (user.getSkills().add(skill)) {
			userRepository.save(user);
		}

		return mapToResponse(skill);
	}

	// Get user's skills
	@Override
	@Transactional(readOnly = true)
	public List<SkillResponse> getUserSkills(Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		return user.getSkills().stream().map(this::mapToResponse).toList();
	}

	// Remove skill from user
	@Override
	public void removeSkillFromUser(Long userId, Long skillId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		Skill skill = skillRepository.findById(skillId)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

		if (!user.getSkills().remove(skill)) {
			throw new BadRequestException("This skill is not assigned to the user");
		}

		userRepository.save(user);
	}

	// Add required skill to job
	@Override
	public SkillResponse addSkillToJob(Long jobId, Long skillId) {

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

		Skill skill = skillRepository.findById(skillId)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

		if (job.getRequiredSkills().add(skill)) {
			jobRepository.save(job);
		}

		return mapToResponse(skill);
	}

	// Get job's required skills
	@Override
	@Transactional(readOnly = true)
	public List<SkillResponse> getJobSkills(Long jobId) {

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

		return job.getRequiredSkills().stream().map(this::mapToResponse).toList();
	}

	// Remove required skill from job
	@Override
	public void removeSkillFromJob(Long jobId, Long skillId) {

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

		Skill skill = skillRepository.findById(skillId)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

		if (!job.getRequiredSkills().remove(skill)) {
			throw new BadRequestException("This skill is not assigned to the job");
		}

		jobRepository.save(job);
	}

	// Entity to DTO mapping
	private SkillResponse mapToResponse(Skill skill) {

		return new SkillResponse(skill.getId(), skill.getSkillName());
	}
}
