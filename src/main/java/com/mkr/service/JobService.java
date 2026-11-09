
package com.mkr.service;

import java.util.List;

import com.mkr.dto.CreateJobRequest;
import com.mkr.dto.JobFilterRequest;
import com.mkr.dto.JobResponse;
import com.mkr.dto.UpdateJobRequest;

public interface JobService {

	JobResponse createJob(Long recruiterId, CreateJobRequest request);

	List<JobResponse> getAllJobs();

	JobResponse updateJob(Long jobId, Long recruiterId,
	                      UpdateJobRequest request);

	void deleteJob(Long jobId, Long recruiterId);

	void closeJob(Long jobId, Long recruiterId);

	void reopenJob(Long jobId, Long recruiterId);

	List<JobResponse> getOpenJobs();
	
	List<JobResponse> searchJobs(String keyword);
	
	List<JobResponse> filterJobs(JobFilterRequest request);// all filters 
}
