
package com.mkr.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String skillName;

    // Skills associated with users
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Jobs requiring this skill
    @ManyToMany(mappedBy = "requiredSkills", fetch = FetchType.LAZY)
    private Set<Job> jobs = new HashSet<>();
}
