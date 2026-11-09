package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mkr.dto.ApplicationResponse;
import com.mkr.enums.ApplicationStatus;
import com.mkr.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService applicationService;

    // Job Seeker applies for a job
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        ApplicationResponse response =
                applicationService.applyForJob(
                        jobId, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get logged-in Job Seeker's applications
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {

        List<ApplicationResponse> applications =
                applicationService.getMyApplications(
                        authentication.getName());

        return ResponseEntity.ok(applications);
    }

    // Get application by ID
    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Long applicationId,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(
                        applicationId, authentication.getName()));
    }

    // Withdraw an application
    @PatchMapping("/{applicationId}/withdraw")
    public ResponseEntity<ApplicationResponse> withdrawApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {

        ApplicationResponse response =
                applicationService.withdrawApplication(
                        applicationId, authentication.getName());

        return ResponseEntity.ok(response);
    }

    // Recruiter views applications for their job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        List<ApplicationResponse> applications =
                applicationService.getApplicationsByJob(
                        jobId, authentication.getName());

        return ResponseEntity.ok(applications);
    }

    // Recruiter updates application status
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        ApplicationResponse response =
                applicationService.updateApplicationStatus(
                        applicationId,
                        authentication.getName(),
                        status);

        return ResponseEntity.ok(response);
    }
}








                     /// before JWT Implementation 
//package com.mkr.controller;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.mkr.dto.ApplicationResponse;
//import com.mkr.enums.ApplicationStatus;
//import com.mkr.service.JobApplicationService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/applications")
//@RequiredArgsConstructor
//public class JobApplicationController {
//
//    private final JobApplicationService applicationService;
//
//    // Apply for a job
//    @PostMapping("/apply/{jobId}")
//    public ResponseEntity<ApplicationResponse> applyForJob(
//            @PathVariable Long jobId,
//            @RequestParam Long jobSeekerId) {
//
//        ApplicationResponse response =
//                applicationService.applyForJob(jobId, jobSeekerId);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    // Get all applications submitted by a job seeker
//    @GetMapping("/my")
//    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
//            @RequestParam Long jobSeekerId) {
//
//        List<ApplicationResponse> applications =
//                applicationService.getMyApplications(jobSeekerId);
//
//        return ResponseEntity.ok(applications);
//    }
//
//    // Get application details by ID
////    @GetMapping("/{applicationId}")
////    public ResponseEntity<ApplicationResponse> getApplicationById(
////            @PathVariable Long applicationId) {
////
////        ApplicationResponse response =
////                applicationService.getApplicationById(applicationId);
////
////        return ResponseEntity.ok(response);
////    }
//    
//    @GetMapping("/{applicationId}")
//    public ResponseEntity<ApplicationResponse> getApplicationById(
//            @PathVariable Long applicationId,
//            @RequestParam Long userId) {
//
//        return ResponseEntity.ok(
//                applicationService.getApplicationById(
//                        applicationId, userId));
//    }
//
//    // Withdraw an application
//    @PatchMapping("/{applicationId}/withdraw")
//    public ResponseEntity<ApplicationResponse> withdrawApplication(
//            @PathVariable Long applicationId,
//            @RequestParam Long jobSeekerId) {
//
//        ApplicationResponse response =
//                applicationService.withdrawApplication(
//                        applicationId, jobSeekerId);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // Recruiter views applications for a particular job
//    @GetMapping("/job/{jobId}")
//    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
//            @PathVariable Long jobId,
//            @RequestParam Long recruiterId) {
//
//        List<ApplicationResponse> applications =
//                applicationService.getApplicationsByJob(jobId, recruiterId);
//
//        return ResponseEntity.ok(applications);
//    }
//    
// // Recruiter updates application status
//    @PatchMapping("/{applicationId}/status")
//    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
//            @PathVariable Long applicationId,
//            @RequestParam Long recruiterId,
//            @RequestParam ApplicationStatus status) {
//
//        ApplicationResponse response =
//                applicationService.updateApplicationStatus(
//                        applicationId, recruiterId, status);
//
//        return ResponseEntity.ok(response);
//    }
//}
