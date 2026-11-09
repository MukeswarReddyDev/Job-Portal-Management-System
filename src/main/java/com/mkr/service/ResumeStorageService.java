package com.mkr.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeStorageService {

    String storeResume(MultipartFile file);

    void deleteResume(String filePath);
}