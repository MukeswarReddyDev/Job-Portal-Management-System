
package com.mkr.service;

import java.util.List;

import com.mkr.dto.ApplicationResponse;
import com.mkr.enums.ApplicationStatus;

public interface JobApplicationService {

    ApplicationResponse applyForJob(Long jobId, String email);

    List<ApplicationResponse> getMyApplications(String email);

    ApplicationResponse getApplicationById(
            Long applicationId, String email);

    ApplicationResponse withdrawApplication(
            Long applicationId, String email);

    List<ApplicationResponse> getApplicationsByJob(
            Long jobId, String email);

    ApplicationResponse updateApplicationStatus(
            Long applicationId,
            String email,
            ApplicationStatus status
    );
}







                               ///  Before JWT Implementation 
//package com.mkr.service;
//
//import java.util.List;
//
//import com.mkr.dto.ApplicationResponse;
//import com.mkr.enums.ApplicationStatus;
//
//public interface JobApplicationService {
//
//    // Apply for a job
//    ApplicationResponse applyForJob(
//            Long jobId,
//            Long jobSeekerId
//    );
//
//    // Get all applications submitted by a job seeker
//    List<ApplicationResponse> getMyApplications(Long jobSeekerId);
//
//    // Get application details by ID
//   // ApplicationResponse getApplicationById(Long applicationId);
//    
//    ApplicationResponse getApplicationById(Long applicationId, Long userId);
//
//    // Withdraw an application
//    ApplicationResponse withdrawApplication(
//            Long applicationId,
//            Long jobSeekerId
//    );
//
//    // Recruiter views applications for a particular job
//    List<ApplicationResponse> getApplicationsByJob(
//            Long jobId,
//            Long recruiterId
//    );
//    
//    
//    // to update the application status 
//    ApplicationResponse updateApplicationStatus(
//            Long applicationId,
//            Long recruiterId,
//            ApplicationStatus status
//    );
//}
