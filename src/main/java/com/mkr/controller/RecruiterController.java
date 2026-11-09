
package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mkr.dto.RecruiterResponse;
import com.mkr.dto.RegisterRecruiterRequest;
import com.mkr.enums.RecruiterStatus;
import com.mkr.service.RecruiterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;

    // 1. Recruiter self-registration
    @PostMapping("/register")
    public ResponseEntity<RecruiterResponse> registerRecruiter(
            @Valid @RequestBody RegisterRecruiterRequest request) {

        RecruiterResponse response =
                recruiterService.registerRecruiter(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get recruiter profile using User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<RecruiterResponse> getRecruiterByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                recruiterService.getRecruiterByUserId(userId));
    }

    // 3. Get recruiters by approval status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RecruiterResponse>> getRecruitersByStatus(
            @PathVariable RecruiterStatus status) {

        return ResponseEntity.ok(
                recruiterService.getRecruitersByStatus(status));
    }

    // 4. Admin approves recruiter
    @PatchMapping("/approve/{userId}")
    public ResponseEntity<RecruiterResponse> approveRecruiter(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                recruiterService.approveRecruiter(userId));
    }

    // 5. Admin rejects recruiter
    @PatchMapping("/reject/{userId}")
    public ResponseEntity<RecruiterResponse> rejectRecruiter(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                recruiterService.rejectRecruiter(userId));
    }
}
