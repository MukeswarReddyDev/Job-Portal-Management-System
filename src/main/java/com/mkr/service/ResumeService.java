
package com.mkr.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mkr.dto.ResumeResponse;

public interface ResumeService {

    ResumeResponse uploadResume(Long userId, MultipartFile file);

    List<ResumeResponse> getUserResumes(Long userId);

    ResumeResponse getPrimaryResume(Long userId);

    ResumeResponse setPrimaryResume(Long userId, Long resumeId);

    void deleteResume(Long userId, Long resumeId);
}