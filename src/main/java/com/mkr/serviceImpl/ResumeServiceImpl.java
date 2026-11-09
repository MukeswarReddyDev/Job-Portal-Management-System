
package com.mkr.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mkr.dto.ResumeResponse;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.Resume;
import com.mkr.model.User;
import com.mkr.repository.ResumeRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.ResumeService;
import com.mkr.service.ResumeStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

	private final ResumeRepository resumeRepository;
	private final UserRepository userRepository;
	private final ResumeStorageService resumeStorageService;

	@Override
	@Transactional
	public ResumeResponse uploadResume(Long userId, MultipartFile file) {

		// Check whether the user exists
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

		// Store the physical PDF file
		String filePath = resumeStorageService.storeResume(file);

		// Create Resume entity
		Resume resume = new Resume();

		resume.setFileName(file.getOriginalFilename());
		resume.setFileType(file.getContentType());
		resume.setFilePath(filePath);
		resume.setUser(user);

		// First resume automatically becomes primary
		boolean isFirstResume = resumeRepository.findByUserIdAndIsPrimaryTrue(userId).isEmpty();

		resume.setPrimary(isFirstResume);

		// Save resume details in MySQL
		Resume savedResume = resumeRepository.save(resume);

		return mapToResumeResponse(savedResume);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResumeResponse> getUserResumes(Long userId) {

		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

		return resumeRepository.findByUserId(userId).stream().map(this::mapToResumeResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ResumeResponse getPrimaryResume(Long userId) {

		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

		Resume resume = resumeRepository.findByUserIdAndIsPrimaryTrue(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Primary resume not found for user ID: " + userId));

		return mapToResumeResponse(resume);
	}

	@Override
	@Transactional
	public ResumeResponse setPrimaryResume(Long userId, Long resumeId) {

		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

		// Find the requested resume
		Resume selectedResume = resumeRepository.findById(resumeId)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

		// Verify that this resume belongs to the requested user
		if (!selectedResume.getUser().getId().equals(userId)) {
			throw new ResourceNotFoundException("Resume does not belong to user ID: " + userId);
		}

		// Make all existing resumes non-primary
		List<Resume> resumes = resumeRepository.findByUserId(userId);

		for (Resume resume : resumes) {
			resume.setPrimary(false);
		}

		// Make the selected resume primary
		selectedResume.setPrimary(true);

		resumeRepository.saveAll(resumes);

		return mapToResumeResponse(selectedResume);
	}

	private ResumeResponse mapToResumeResponse(Resume resume) {

		return new ResumeResponse(resume.getId(), resume.getFileName(), resume.getFileType(), resume.isPrimary(),
				resume.getUploadedAt());
	}

	@Override
	@Transactional
	public void deleteResume(Long userId, Long resumeId) {

		// 1. Check whether the user exists
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

		// 2. Find the resume
		Resume resume = resumeRepository.findById(resumeId)
				.orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

		// 3. Verify resume ownership
		if (!resume.getUser().getId().equals(userId)) {
			throw new ResourceNotFoundException("Resume does not belong to user ID: " + userId);
		}

		// 4. Check whether the resume is primary
		boolean wasPrimary = resume.isPrimary();

		// 5. Delete physical PDF file
		resumeStorageService.deleteResume(resume.getFilePath());

		// 6. Delete resume record from database
		resumeRepository.delete(resume);

		// 7. If primary resume was deleted, select another resume
		if (wasPrimary) {

			List<Resume> remainingResumes = resumeRepository.findByUserId(userId);

			if (!remainingResumes.isEmpty()) {

				Resume newPrimary = remainingResumes.get(0);

				newPrimary.setPrimary(true);

				resumeRepository.save(newPrimary);
			}
		}
	}

}
