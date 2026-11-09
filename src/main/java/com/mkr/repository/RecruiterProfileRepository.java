
package com.mkr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mkr.enums.RecruiterStatus;
import com.mkr.model.RecruiterProfile;

@Repository
public interface RecruiterProfileRepository
        extends JpaRepository<RecruiterProfile, Long> {

    Optional<RecruiterProfile> findByUserId(Long userId);

    List<RecruiterProfile> findByRecruiterStatus(RecruiterStatus recruiterStatus);

    boolean existsByUserId(Long userId);
    
      
    
    
    
    //This will count pending, approved and rejected recruiters.
    long countByRecruiterStatus(RecruiterStatus recruiterStatus); 
}
