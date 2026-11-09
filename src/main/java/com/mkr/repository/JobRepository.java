package com.mkr.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mkr.enums.JobStatus;
import com.mkr.enums.JobType;
import com.mkr.enums.WorkMode;
import com.mkr.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Get jobs by status
    
	 List<Job> findByStatus(JobStatus status);

	 
	 
	 // Search jobs by keyword
	 
	    @Query("""
	        SELECT DISTINCT j FROM Job j
	        WHERE j.status = :status
	        AND (
	            LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
	        )
	    """)
	    List<Job> searchOpenJobs(
	            @Param("status") JobStatus status,
	            @Param("keyword") String keyword
	    );
	    
	    
	    
	    // Flexible Job Search & Filter
	    
	    @Query("""
	    	    SELECT DISTINCT j FROM Job j
	    	    LEFT JOIN j.requiredSkills skill
	    	    WHERE j.status = :status

	    	    AND (
	    	        :keyword IS NULL
	    	        OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    	        OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    	        OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    	        OR LOWER(skill.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    	    )

	    	    AND (
	    	        :location IS NULL
	    	        OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))
	    	    )

	    	    AND (
	    	        :jobType IS NULL
	    	        OR j.jobType = :jobType
	    	    )

	    	    AND (
	    	        :workMode IS NULL
	    	        OR j.workMode = :workMode
	    	    )

	    	    AND (
	    	        :minExperience IS NULL
	    	        OR j.experienceYears >= :minExperience
	    	    )

	    	    AND (
	    	        :maxExperience IS NULL
	    	        OR j.experienceYears <= :maxExperience
	    	    )

	    	    AND (
	    	        :minSalary IS NULL
	    	        OR j.salary >= :minSalary
	    	    )

	    	    AND (
	    	        :maxSalary IS NULL
	    	        OR j.salary <= :maxSalary
	    	    )
	    	""")
	    	List<Job> filterJobs(
	    	        @Param("status") JobStatus status,
	    	        @Param("keyword") String keyword,
	    	        @Param("location") String location,
	    	        @Param("jobType") JobType jobType,
	    	        @Param("workMode") WorkMode workMode,
	    	        @Param("minExperience") Integer minExperience,
	    	        @Param("maxExperience") Integer maxExperience,
	    	        @Param("minSalary") BigDecimal minSalary,
	    	        @Param("maxSalary") BigDecimal maxSalary
	    	);
	    
	    
	    
	    //Your existing count() method provides the total jobs count.
	    long countByStatus(JobStatus status);
	    
	 // Retrieves the latest 5 jobs posted in the Job Portal.
	 // OrderByCreatedAtDesc sorts jobs by creation date in descending order.
	 // Desc means newest jobs appear first.
	 // Used by the Admin Dashboard to display recently posted jobs.

	 List<Job> findTop5ByOrderByCreatedAtDesc();
	 
	// Counts all jobs posted by a particular recruiter.
	 long countByRecruiterId(Long recruiterId);

	 // Counts jobs posted by a recruiter based on their status.
	 long countByRecruiterIdAndStatus(Long recruiterId, JobStatus status);
	 
	 
	 
	 
	}
	
