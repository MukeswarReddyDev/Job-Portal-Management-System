
package com.mkr.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mkr.exception.BadRequestException;
import com.mkr.service.ResumeStorageService;

@Service
public class ResumeStorageServiceImpl implements ResumeStorageService {

	private final Path uploadPath;

	public ResumeStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {

		this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

		try {
			Files.createDirectories(uploadPath);
		} catch (IOException e) {
			throw new RuntimeException("Could not create resume storage directory", e);
		}
	}

	@Override
	public String storeResume(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new BadRequestException("Resume file is empty");
		}

		if (file.getSize() > 5 * 1024 * 1024) {
			throw new BadRequestException("Resume size cannot exceed 5 MB");
		}

		String originalName = file.getOriginalFilename();

		if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")
				|| !"application/pdf".equalsIgnoreCase(file.getContentType())) {

			throw new BadRequestException("Only PDF resumes are allowed");
		}

		String storedName = UUID.randomUUID() + ".pdf";

		try {
			Path targetPath = uploadPath.resolve(storedName).normalize();

			if (!targetPath.startsWith(uploadPath)) {
				throw new BadRequestException("Invalid file path");
			}

			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

			return uploadPath.relativize(targetPath).toString();

		} catch (IOException e) {
			throw new RuntimeException("Failed to store resume", e);
		}
	}

	@Override
	public void deleteResume(String filePath) {

		if (filePath == null || filePath.isBlank()) {
			throw new BadRequestException("Invalid resume file path");
		}

		try {
			// Resolve the file inside the configured upload directory
			Path targetPath = uploadPath.resolve(filePath).toAbsolutePath().normalize();

			// Prevent deleting files outside the upload directory
			if (!targetPath.startsWith(uploadPath)) {
				throw new BadRequestException("Invalid resume file path");
			}

			// Delete the physical PDF file
			Files.deleteIfExists(targetPath);

		} catch (IOException e) {
			throw new RuntimeException("Failed to delete resume file", e);
		}
	}

}
