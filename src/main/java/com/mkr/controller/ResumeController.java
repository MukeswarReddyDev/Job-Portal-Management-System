
package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mkr.dto.ResumeResponse;
import com.mkr.service.ResumeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // Upload a resume
    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {

        ResumeResponse response =
                resumeService.uploadResume(userId, file);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all resumes of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeResponse>> getUserResumes(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                resumeService.getUserResumes(userId)
        );
    }

    // Get primary resume
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ResumeResponse> getPrimaryResume(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                resumeService.getPrimaryResume(userId)
        );
    }

    // Change primary resume
    @PutMapping("/user/{userId}/primary/{resumeId}")
    public ResponseEntity<ResumeResponse> setPrimaryResume(
            @PathVariable Long userId,
            @PathVariable Long resumeId) {

        return ResponseEntity.ok(
                resumeService.setPrimaryResume(userId, resumeId)
        );
    }
    
    @DeleteMapping("/user/{userId}/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable Long userId,
            @PathVariable Long resumeId) {

        resumeService.deleteResume(userId, resumeId);

        return ResponseEntity.noContent().build();
    }
}
