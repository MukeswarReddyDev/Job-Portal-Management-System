
package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mkr.dto.CreateJobRequest;
import com.mkr.dto.JobFilterRequest;
import com.mkr.dto.JobResponse;
import com.mkr.dto.UpdateJobRequest;
import com.mkr.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // 1. Create Job
    @PostMapping("/createJob")
    public ResponseEntity<JobResponse> createJob(
            @RequestParam Long recruiterId,
            @Valid @RequestBody CreateJobRequest request) {

        JobResponse response = jobService.createJob(
                recruiterId, request
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get All Jobs
    @GetMapping("/getAllJobs")
    public ResponseEntity<List<JobResponse>> getAllJobs() {

        List<JobResponse> jobs = jobService.getAllJobs();

        return ResponseEntity.ok(jobs);
    }

    // 3. Update Job
    @PutMapping("/updateJob/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @RequestParam Long recruiterId,
            @Valid @RequestBody UpdateJobRequest request) {

        JobResponse response = jobService.updateJob(
                jobId, recruiterId, request
        );

        return ResponseEntity.ok(response);
    }
    
    
 // 4. Delete Job
    @DeleteMapping("/deleteJob/{jobId}")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long jobId,
            @RequestParam Long recruiterId) {

        jobService.deleteJob(jobId, recruiterId);

        return ResponseEntity.ok("Job deleted successfully");
    }
    
 // 5. Close Job
    @PatchMapping("/closeJob/{jobId}")
    public ResponseEntity<String> closeJob(
            @PathVariable Long jobId,
            @RequestParam Long recruiterId) {

        jobService.closeJob(jobId, recruiterId);

        return ResponseEntity.ok("Job closed successfully");
    }

    // 6. Reopen Job
    @PatchMapping("/reopenJob/{jobId}")
    public ResponseEntity<String> reopenJob(
            @PathVariable Long jobId,
            @RequestParam Long recruiterId) {

        jobService.reopenJob(jobId, recruiterId);

        return ResponseEntity.ok("Job reopened successfully");
    }
    
 // 7. Get All Open Jobs
    @GetMapping("/getOpenJobs")
    public ResponseEntity<List<JobResponse>> getOpenJobs() {

        List<JobResponse> jobs = jobService.getOpenJobs();

        return ResponseEntity.ok(jobs);
    }
    
   // Search for a Job BY Keywords
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam String keyword) {

        List<JobResponse> jobs = jobService.searchJobs(keyword);

        return ResponseEntity.ok(jobs);
    }
    
    // filter the jobs based on all the filters 
    
    @GetMapping("/filter")
    public ResponseEntity<List<JobResponse>> filterJobs(
            @ModelAttribute JobFilterRequest request) {

        List<JobResponse> jobs = jobService.filterJobs(request);

        return ResponseEntity.ok(jobs);
        
        /*
         * ============================================
         * JOB FILTER API - POSTMAN TEST CASES
         * ============================================
         *
         * API:
         * GET http://localhost:8080/api/jobs/filter
         *
         * TEST 1: Keyword only
         * http://localhost:8080/api/jobs/filter?keyword=java
         *
         * TEST 2: Location and keyword
         * http://localhost:8080/api/jobs/filter?keyword=spring&location=Bangalore
         *
         * TEST 3: Job type and work mode
         * http://localhost:8080/api/jobs/filter?jobType=FULL_TIME&workMode=REMOTE
         *
         * TEST 4: Experience range
         * http://localhost:8080/api/jobs/filter?minExperience=1&maxExperience=3
         *
         * TEST 5: Salary range
         * http://localhost:8080/api/jobs/filter?minSalary=400000&maxSalary=1000000
         *
         * TEST 6: Complete filtering
         * http://localhost:8080/api/jobs/filter?keyword=java&location=Bangalore&jobType=FULL_TIME&workMode=REMOTE&minExperience=1&maxExperience=5&minSalary=400000&maxSalary=1200000
         *
         * TEST 7: No filters
         * http://localhost:8080/api/jobs/filter
         *
         * EXPECTED BEHAVIOUR:
         * - All filter parameters are optional.
         * - Users can combine multiple filters.
         * - Only OPEN jobs should be returned.
         * - If no filters are provided, return all OPEN jobs.
         *
         * ============================================
         */ 
       
    }
}
