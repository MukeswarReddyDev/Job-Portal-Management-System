
package com.mkr.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mkr.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One recruiter can post multiple jobs
    @OneToMany(mappedBy = "recruiter", fetch = FetchType.LAZY)
    private List<Job> postedJobs = new ArrayList<>();

    // One job seeker can submit multiple applications
    @OneToMany(mappedBy = "jobSeeker", fetch = FetchType.LAZY)
    private List<JobApplication> applications = new ArrayList<>();

    // One user can have multiple skills
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_skills",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"),
        uniqueConstraints = @UniqueConstraint(
            columnNames = {"user_id", "skill_id"}
        )
    )
    private Set<Skill> skills = new HashSet<>();

    // One user can upload multiple resumes
    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Resume> resumes = new ArrayList<>();
    
    
    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
    
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
  private RecruiterProfile recruiterProfile;
}
