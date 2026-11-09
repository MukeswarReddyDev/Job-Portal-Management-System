package com.mkr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mkr.enums.Role;
import com.mkr.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<User> findByFullNameContainingIgnoreCase(String fullName);

    List<User> findByEmailContainingIgnoreCase(String email);

    List<User> findByRole(Role role);

    List<User> findByFullNameContainingIgnoreCaseAndRole(
            String fullName, Role role
    );
    
    boolean existsByEmailIgnoreCase(String email); // for recruiter 
    
    Optional<User> findByEmailIgnoreCase(String email);
    
    long countByRole(Role role);

    long countByActiveTrue();
}