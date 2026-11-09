
package com.mkr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mkr.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Get all resumes uploaded by a particular user
    List<Resume> findByUserId(Long userId);

    // Get the primary resume of a particular user
    Optional<Resume> findByUserIdAndIsPrimaryTrue(Long userId);

    // Check whether a resume belongs to a particular user
    boolean existsByIdAndUserId(Long resumeId, Long userId);
}
