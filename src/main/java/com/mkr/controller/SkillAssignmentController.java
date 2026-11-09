
package com.mkr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mkr.dto.SkillResponse;
import com.mkr.service.SkillAssignmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SkillAssignmentController {

    private final SkillAssignmentService skillAssignmentService;

    // Add skill to user
    @PostMapping("/users/{userId}/skills/{skillId}")
    public ResponseEntity<SkillResponse> addSkillToUser(
            @PathVariable Long userId,
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillAssignmentService.addSkillToUser(userId, skillId));
    }

    // Get user's skills
    @GetMapping("/users/{userId}/skills")
    public ResponseEntity<List<SkillResponse>> getUserSkills(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                skillAssignmentService.getUserSkills(userId));
    }

    // Remove skill from user
    @DeleteMapping("/users/{userId}/skills/{skillId}")
    public ResponseEntity<Void> removeSkillFromUser(
            @PathVariable Long userId,
            @PathVariable Long skillId) {

        skillAssignmentService.removeSkillFromUser(userId, skillId);

        return ResponseEntity.noContent().build();
    }

    // Add required skill to job
    @PostMapping("/jobs/{jobId}/skills/{skillId}")
    public ResponseEntity<SkillResponse> addSkillToJob(
            @PathVariable Long jobId,
            @PathVariable Long skillId) {

        return ResponseEntity.ok(
                skillAssignmentService.addSkillToJob(jobId, skillId));
    }

    // Get job's required skills
    @GetMapping("/jobs/{jobId}/skills")
    public ResponseEntity<List<SkillResponse>> getJobSkills(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                skillAssignmentService.getJobSkills(jobId));
    }

    // Remove required skill from job
    @DeleteMapping("/jobs/{jobId}/skills/{skillId}")
    public ResponseEntity<Void> removeSkillFromJob(
            @PathVariable Long jobId,
            @PathVariable Long skillId) {

        skillAssignmentService.removeSkillFromJob(jobId, skillId);

        return ResponseEntity.noContent().build();
    }
}
