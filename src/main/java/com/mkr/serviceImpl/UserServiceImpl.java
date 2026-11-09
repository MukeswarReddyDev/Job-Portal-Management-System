//

package com.mkr.serviceImpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mkr.dto.ChangePasswordRequest;
import com.mkr.dto.ProfileCompletionResponse;
import com.mkr.dto.RegisterRequest;
import com.mkr.dto.UpdateSkillsRequest;
import com.mkr.dto.UpdateUserRequest;
import com.mkr.dto.UserResponse;
import com.mkr.enums.Role;
import com.mkr.exception.BadRequestException;
import com.mkr.exception.DuplicateResourceException;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.Skill;
import com.mkr.model.User;
import com.mkr.repository.SkillRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.ResumeService;
import com.mkr.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResumeService resumeService;

    // 1. Register Job Seeker

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {

        // Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email is already registered");
        }

        // Public registration is only for JOB_SEEKER
        Role role = request.getRole() == null
                ? Role.JOB_SEEKER
                : request.getRole();

        if (role != Role.JOB_SEEKER) {
            throw new BadRequestException(
                    "Only JOB_SEEKER registration is allowed");
        }

        // Validate skills
        if (request.getSkills() == null
                || request.getSkills().isEmpty()) {

            throw new BadRequestException(
                    "Please provide at least one skill");
        }

        // Normalize skills and remove duplicates
        Set<String> normalizedSkills = new LinkedHashSet<>();

        for (String skillName : request.getSkills()) {

            if (skillName == null || skillName.isBlank()) {
                throw new BadRequestException(
                        "Skill name cannot be empty");
            }

            normalizedSkills.add(
                    skillName.trim().toUpperCase(Locale.ROOT));
        }

        // Create User entity
        User user = new User();

        BeanUtils.copyProperties(
                request,
                user,
                "email",
                "password",
                "role",
                "resume",
                "skills");

        user.setEmail(email);

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        user.setRole(role);

        // Find existing skills or create new skills
        for (String skillName : normalizedSkills) {

            Skill skill = skillRepository
                    .findBySkillNameIgnoreCase(skillName)
                    .orElseGet(() -> {

                        Skill newSkill = new Skill();
                        newSkill.setSkillName(skillName);

                        return skillRepository.save(newSkill);
                    });

            // Assign skill to user
            user.getSkills().add(skill);
        }

        // Save user with skills
        User savedUser = userRepository.save(user);

        // Upload optional resume
        if (request.getResume() != null
                && !request.getResume().isEmpty()) {

            resumeService.uploadResume(
                    savedUser.getId(),
                    request.getResume());
        }

        return mapToUserResponse(savedUser);
    }

    // 2. Get All Users (Admin)

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // 3. Get User By ID (Admin)

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id));

        return mapToUserResponse(user);
    }

    // 4. Get User By JWT Email

    private User getUserByEmail(String email) {

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    // 5. Get Logged-in User Profile

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {

        User user = getUserByEmail(email);

        return mapToUserResponse(user);
    }

    // 6. Update Logged-in User Profile

    @Override
    @Transactional
    public UserResponse updateUser(
            String email,
            UpdateUserRequest request) {

        // Identify user using JWT email
        User user = getUserByEmail(email);

        Long id = user.getId();

        // Only job seekers can update their profile
        if (user.getRole() != Role.JOB_SEEKER) {
            throw new BadRequestException(
                    "Only job seekers can update this profile");
        }

        // Check which fields are provided
        boolean hasName = request.getFullName() != null;
        boolean hasEmail = request.getEmail() != null;
        boolean hasSkills = request.getSkills() != null;

        boolean hasResume = request.getResume() != null
                && !request.getResume().isEmpty();

        if (!hasName && !hasEmail && !hasSkills && !hasResume) {
            throw new BadRequestException(
                    "Please provide at least one field to update");
        }

        // Update name
        if (hasName) {

            if (request.getFullName().isBlank()) {
                throw new BadRequestException(
                        "Full name cannot be empty");
            }

            user.setFullName(
                    request.getFullName().trim());
        }

        // Update email
        if (hasEmail) {

            if (request.getEmail().isBlank()) {
                throw new BadRequestException(
                        "Email cannot be empty");
            }

            String newEmail = request.getEmail()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (userRepository.existsByEmailAndIdNot(
                    newEmail, id)) {

                throw new DuplicateResourceException(
                        "Email is already registered");
            }

            user.setEmail(newEmail);
        }

        // Update skills
        if (hasSkills) {

            if (request.getSkills().isEmpty()) {
                throw new BadRequestException(
                        "At least one skill is required");
            }

            Set<String> normalizedSkills =
                    new LinkedHashSet<>();

            for (String skillName : request.getSkills()) {

                if (skillName == null || skillName.isBlank()) {
                    throw new BadRequestException(
                            "Skill name cannot be empty");
                }

                normalizedSkills.add(
                        skillName.trim()
                                .toUpperCase(Locale.ROOT));
            }

            // Build updated skill set
            Set<Skill> updatedSkills =
                    new LinkedHashSet<>();

            for (String skillName : normalizedSkills) {

                Skill skill = skillRepository
                        .findBySkillNameIgnoreCase(skillName)
                        .orElseGet(() -> {

                            Skill newSkill = new Skill();
                            newSkill.setSkillName(skillName);

                            return skillRepository.save(newSkill);
                        });

                updatedSkills.add(skill);
            }

            // Replace user's skill associations
            user.getSkills().clear();
            user.getSkills().addAll(updatedSkills);
        }

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Upload new resume if provided
        if (hasResume) {

            resumeService.uploadResume(
                    updatedUser.getId(),
                    request.getResume());
        }

        return mapToUserResponse(updatedUser);
    }

    // 7. Deactivate Logged-in User

    @Override
    @Transactional
    public void deactivateUser(String email) {

        User user = getUserByEmail(email);

        user.setActive(false);

        userRepository.save(user);
    }

    // 8. Search Users By Name Or Email

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(
                    "Search keyword cannot be empty");
        }

        List<User> users =
                userRepository.findByFullNameContainingIgnoreCase(
                        keyword.trim());

        if (users.isEmpty()) {

            users = userRepository.findByEmailContainingIgnoreCase(
                    keyword.trim());
        }

        return users.stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // 9. Filter Users By Role

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {

        if (role == null) {
            throw new BadRequestException(
                    "Role is required");
        }

        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // 10. Search Users By Name And Role

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsersByNameAndRole(
            String keyword,
            Role role) {

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(
                    "Search keyword cannot be empty");
        }

        if (role == null) {
            throw new BadRequestException(
                    "Role is required");
        }

        return userRepository
                .findByFullNameContainingIgnoreCaseAndRole(
                        keyword.trim(), role)
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    // 11. Common UserResponse Mapping Method

    private UserResponse mapToUserResponse(User user) {

        List<String> skills = user.getSkills()
                .stream()
                .map(Skill::getSkillName)
                .sorted()
                .toList();

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                skills);
    }

    // 12. Change Logged-in User Password

    @Override
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = getUserByEmail(email);

        // Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Current password is incorrect");
        }

        // Check new password and confirm password
        if (!request.getNewPassword().equals(
                request.getConfirmPassword())) {

            throw new BadRequestException(
                    "New password and confirm password do not match");
        }

        // Prevent reusing current password
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "New password must be different from current password");
        }

        // Encode and update new password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);
    }

    // 13. Add Skills To Logged-in User

    @Override
    @Transactional
    public void addSkills(
            String email,
            UpdateSkillsRequest request) {

        User user = getUserByEmail(email);

        if (user.getRole() != Role.JOB_SEEKER) {
            throw new BadRequestException(
                    "Only job seekers can add skills");
        }

        for (String skillName : request.getSkills()) {

            String normalizedName = skillName
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (normalizedName.isBlank()) {
                continue;
            }

            Skill skill = skillRepository
                    .findBySkillNameIgnoreCase(normalizedName)
                    .orElseGet(() -> {

                        Skill newSkill = new Skill();
                        newSkill.setSkillName(normalizedName);

                        return skillRepository.save(newSkill);
                    });

            user.getSkills().add(skill);
        }

        userRepository.save(user);
    }

    // 14. Remove Skills From Logged-in User

    @Override
    @Transactional
    public void removeSkills(
            String email,
            UpdateSkillsRequest request) {

        User user = getUserByEmail(email);

        if (user.getRole() != Role.JOB_SEEKER) {
            throw new BadRequestException(
                    "Only job seekers can remove skills");
        }

        Set<String> skillsToRemove = request.getSkills()
                .stream()
                .map(skill ->
                        skill.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        Set<Skill> existingSkills = user.getSkills();

        existingSkills.removeIf(skill ->
                skillsToRemove.contains(
                        skill.getSkillName()
                                .toUpperCase(Locale.ROOT)));

        if (existingSkills.isEmpty()) {
            throw new BadRequestException(
                    "A job seeker must have at least one skill");
        }

        userRepository.save(user);
    }

    // 15. Get Skills Of Logged-in User

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserSkills(String email) {

        User user = getUserByEmail(email);

        return user.getSkills()
                .stream()
                .map(Skill::getSkillName)
                .sorted()
                .toList();
    }

    // 16. Get Logged-in User Profile Completion

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionResponse getProfileCompletion(
            String email) {

        User user = getUserByEmail(email);

        if (user.getRole() != Role.JOB_SEEKER) {
            throw new BadRequestException(
                    "Profile completion is available only for job seekers");
        }

        List<String> missingFields = new ArrayList<>();

        if (user.getFullName() == null
                || user.getFullName().isBlank()) {

            missingFields.add("Full Name");
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            missingFields.add("Email");
        }

        if (user.getSkills() == null
                || user.getSkills().isEmpty()) {

            missingFields.add("Skills");
        }

        if (user.getResumes() == null
                || user.getResumes().isEmpty()) {

            missingFields.add("Resume");
        }

        int totalFields = 4;

        int completedFields =
                totalFields - missingFields.size();

        int percentage =
                (completedFields * 100) / totalFields;

        return new ProfileCompletionResponse(
                percentage,
                completedFields,
                totalFields,
                missingFields);
    }
}




























//package com.mkr.serviceImpl;
//
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.mkr.dto.ChangePasswordRequest;
//import com.mkr.dto.ProfileCompletionResponse;
//import com.mkr.dto.RegisterRequest;
//import com.mkr.dto.UpdateSkillsRequest;
//import com.mkr.dto.UpdateUserRequest;
//import com.mkr.dto.UserResponse;
//import com.mkr.enums.Role;
//import com.mkr.exception.BadRequestException;
//import com.mkr.exception.DuplicateResourceException;
//import com.mkr.exception.ResourceNotFoundException;
//import com.mkr.model.Skill;
//import com.mkr.model.User;
//import com.mkr.repository.SkillRepository;
//import com.mkr.repository.UserRepository;
//import com.mkr.service.ResumeService;
//import com.mkr.service.UserService;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//	private final UserRepository userRepository;
//	private final SkillRepository skillRepository;
//	private final PasswordEncoder passwordEncoder;
//	private final ResumeService resumeService;
//
//	// 1. Register Job Seeker
//
//	@Override
//	@Transactional
//	public UserResponse registerUser(RegisterRequest request) {
//
//		// Normalize email
//		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
//
//		// Check duplicate email
//		if (userRepository.existsByEmail(email)) {
//			throw new DuplicateResourceException("Email is already registered");
//		}
//
//		// Public registration is only for JOB_SEEKER
//		Role role = request.getRole() == null ? Role.JOB_SEEKER : request.getRole();
//
//		if (role != Role.JOB_SEEKER) {
//			throw new BadRequestException("Only JOB_SEEKER registration is allowed");
//		}
//
//		// Validate skills
//		if (request.getSkills() == null || request.getSkills().isEmpty()) {
//
//			throw new BadRequestException("Please provide at least one skill");
//		}
//
//		// Normalize skills and remove duplicates
//		Set<String> normalizedSkills = new LinkedHashSet<>();
//
//		for (String skillName : request.getSkills()) {
//
//			if (skillName == null || skillName.isBlank()) {
//				throw new BadRequestException("Skill name cannot be empty");
//			}
//
//			normalizedSkills.add(skillName.trim().toUpperCase(Locale.ROOT));
//		}
//
//		// Create User entity
//		User user = new User();
//
//		BeanUtils.copyProperties(request, user, "email", "password", "role", "resume", "skills");
//
//		user.setEmail(email);
//
//		// Encrypt password
//		user.setPassword(passwordEncoder.encode(request.getPassword()));
//
//		user.setRole(role);
//
//		// Find existing skills or create new skills
//		for (String skillName : normalizedSkills) {
//
//			Skill skill = skillRepository.findBySkillNameIgnoreCase(skillName).orElseGet(() -> {
//
//				Skill newSkill = new Skill();
//				newSkill.setSkillName(skillName);
//
//				return skillRepository.save(newSkill);
//			});
//
//			// Assign skill to user
//			user.getSkills().add(skill);
//		}
//
//		// Save user with skills
//		User savedUser = userRepository.save(user);
//
//		// Upload optional resume
//		if (request.getResume() != null && !request.getResume().isEmpty()) {
//
//			resumeService.uploadResume(savedUser.getId(), request.getResume());
//		}
//
//		// Return response
//		return mapToUserResponse(savedUser);
//	}
//
//	// 2. Get All Users
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<UserResponse> getAllUsers() {
//
//		return userRepository.findAll().stream().map(this::mapToUserResponse).toList();
//	}
//
//	// 3. Get User By ID
//
//	@Override
//	@Transactional(readOnly = true)
//	public UserResponse getUserById(Long id) {
//
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
//
//		return mapToUserResponse(user);
//	}
//
//	// 4. Update User
//
//
//
//@Override
//@Transactional
//public UserResponse updateUser(Long id, UpdateUserRequest request) {
//
//    // 1. Find user
//    User user = userRepository.findById(id)
//            .orElseThrow(() ->
//                    new ResourceNotFoundException(
//                            "User not found with ID: " + id));
//
//    // 2. Only job seekers can update their profile
//    if (user.getRole() != Role.JOB_SEEKER) {
//        throw new BadRequestException(
//                "Only job seekers can update this profile");
//    }
//
//    // 3. Check which fields are provided
//    boolean hasName = request.getFullName() != null;
//    boolean hasEmail = request.getEmail() != null;
//    boolean hasSkills = request.getSkills() != null;
//    boolean hasResume = request.getResume() != null
//            && !request.getResume().isEmpty();
//
//    if (!hasName && !hasEmail && !hasSkills && !hasResume) {
//        throw new BadRequestException(
//                "Please provide at least one field to update");
//    }
//
//    // 4. Update name
//    if (hasName) {
//
//        if (request.getFullName().isBlank()) {
//            throw new BadRequestException(
//                    "Full name cannot be empty");
//        }
//
//        user.setFullName(request.getFullName().trim());
//    }
//
//    // 5. Update email
//    if (hasEmail) {
//
//        if (request.getEmail().isBlank()) {
//            throw new BadRequestException(
//                    "Email cannot be empty");
//        }
//
//        String email = request.getEmail()
//                .trim()
//                .toLowerCase(Locale.ROOT);
//
//        if (userRepository.existsByEmailAndIdNot(email, id)) {
//            throw new DuplicateResourceException(
//                    "Email is already registered");
//        }
//
//        user.setEmail(email);
//    }
//
//    // 6. Update skills
//    // Send the complete updated list to add or remove skills
//    if (hasSkills) {
//
//        if (request.getSkills().isEmpty()) {
//            throw new BadRequestException(
//                    "At least one skill is required");
//        }
//
//        Set<String> normalizedSkills = new LinkedHashSet<>();
//
//        for (String skillName : request.getSkills()) {
//
//            if (skillName == null || skillName.isBlank()) {
//                throw new BadRequestException(
//                        "Skill name cannot be empty");
//            }
//
//            normalizedSkills.add(
//                    skillName.trim().toUpperCase(Locale.ROOT)
//            );
//        }
//
//        // Build the new skill set first
//        Set<Skill> updatedSkills = new LinkedHashSet<>();
//
//        for (String skillName : normalizedSkills) {
//
//            Skill skill = skillRepository
//                    .findBySkillNameIgnoreCase(skillName)
//                    .orElseGet(() -> {
//
//                        Skill newSkill = new Skill();
//                        newSkill.setSkillName(skillName);
//
//                        return skillRepository.save(newSkill);
//                    });
//
//            updatedSkills.add(skill);
//        }
//
//        // Replace only the user's skill associations
//        user.getSkills().clear();
//        user.getSkills().addAll(updatedSkills);
//    }
//
//    // 7. Save updated user
//    User updatedUser = userRepository.save(user);
//
//    // 8. Upload new resume if provided
//    if (hasResume) {
//
//        resumeService.uploadResume(
//                updatedUser.getId(),
//                request.getResume()
//        );
//    }
//
//    // 9. Return updated profile
//    return mapToUserResponse(updatedUser);
//}
//
//
//	// 5. Deactivate User
//
//	@Override
//	@Transactional
//	public void deactivateUser(Long id) {
//
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
//
//		user.setActive(false);
//
//		userRepository.save(user);
//	}
//
//	// 6. Search Users By Name Or Email
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<UserResponse> searchUsers(String keyword) {
//
//		if (keyword == null || keyword.isBlank()) {
//			throw new BadRequestException("Search keyword cannot be empty");
//		}
//
//		List<User> users = userRepository.findByFullNameContainingIgnoreCase(keyword.trim());
//
//		if (users.isEmpty()) {
//			users = userRepository.findByEmailContainingIgnoreCase(keyword.trim());
//		}
//
//		return users.stream().map(this::mapToUserResponse).toList();
//	}
//
//	// 7. Filter Users By Role
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<UserResponse> getUsersByRole(Role role) {
//
//		if (role == null) {
//			throw new BadRequestException("Role is required");
//		}
//
//		return userRepository.findByRole(role).stream().map(this::mapToUserResponse).toList();
//	}
//
//	// 8. Search Users By Name And Role
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<UserResponse> searchUsersByNameAndRole(String keyword, Role role) {
//
//		if (keyword == null || keyword.isBlank()) {
//			throw new BadRequestException("Search keyword cannot be empty");
//		}
//
//		if (role == null) {
//			throw new BadRequestException("Role is required");
//		}
//
//		return userRepository.findByFullNameContainingIgnoreCaseAndRole(keyword.trim(), role).stream()
//				.map(this::mapToUserResponse).toList();
//	}
//
//	// 9. Common UserResponse Mapping Method
//
//	private UserResponse mapToUserResponse(User user) {
//
//		List<String> skills = user.getSkills().stream().map(Skill::getSkillName).sorted().toList();
//
//		return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt(),
//				skills);
//	}
//
//
//	@Override
//	@Transactional
//	public void changePassword(Long id, ChangePasswordRequest request) {
//
//		// 1. Find user
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
//
//		// 2. Verify current password
//		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
//
//			throw new BadRequestException("Current password is incorrect");
//		}
//
//		// 3. Check whether new password and confirm password match
//		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
//			throw new BadRequestException("New password and confirm password do not match");
//		}
//
//		// 4. Prevent using the same password again
//		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
//
//			throw new BadRequestException("New password must be different from current password");
//		}
//
//		// 5. Encode and update new password
//		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//
//		userRepository.save(user);
//	}
//
//	@Override
//	@Transactional
//	public void addSkills(Long userId, UpdateSkillsRequest request) {
//
//	    User user = userRepository.findById(userId)
//	            .orElseThrow(() ->
//	                new ResourceNotFoundException("User not found with ID: " + userId));
//
//	    if (user.getRole() != Role.JOB_SEEKER) {
//	        throw new BadRequestException("Only job seekers can add skills");
//	    }
//
//	    for (String skillName : request.getSkills()) {
//
//	        String normalizedName = skillName.trim().toUpperCase(Locale.ROOT);
//
//	        if (normalizedName.isBlank()) {
//	            continue;
//	        }
//
//	        Skill skill = skillRepository
//	                .findBySkillNameIgnoreCase(normalizedName)
//	                .orElseGet(() -> {
//	                    Skill newSkill = new Skill();
//	                    newSkill.setSkillName(normalizedName);
//	                    return skillRepository.save(newSkill);
//	                });
//
//	        user.getSkills().add(skill);
//	    }
//
//	    userRepository.save(user);
//	}
//	
//	@Override
//	@Transactional
//	public void removeSkills(Long userId, UpdateSkillsRequest request) {
//
//	    User user = userRepository.findById(userId)
//	            .orElseThrow(() ->
//	                new ResourceNotFoundException(
//	                    "User not found with ID: " + userId));
//
//	    if (user.getRole() != Role.JOB_SEEKER) {
//	        throw new BadRequestException(
//	            "Only job seekers can remove skills");
//	    }
//
//	    Set<String> skillsToRemove = request.getSkills().stream()
//	            .map(skill -> skill.trim().toUpperCase(Locale.ROOT))
//	            .collect(Collectors.toSet());
//
//	    Set<Skill> existingSkills = user.getSkills();
//
//	    existingSkills.removeIf(skill ->
//	            skillsToRemove.contains(
//	                skill.getSkillName().toUpperCase(Locale.ROOT)));
//
//	    if (existingSkills.isEmpty()) {
//	        throw new BadRequestException(
//	            "A job seeker must have at least one skill");
//	    }
//
//	    userRepository.save(user);
//	}
//	
//	
//	@Override
//	@Transactional(readOnly = true)
//	public List<String> getUserSkills(Long userId) {
//
//	    User user = userRepository.findById(userId)
//	            .orElseThrow(() ->
//	                new ResourceNotFoundException(
//	                    "User not found with ID: " + userId));
//
//	    return user.getSkills().stream()
//	            .map(Skill::getSkillName)
//	            .sorted()
//	            .toList();
//	}
//	
//	
//	@Override
//	@Transactional(readOnly = true)
//	public ProfileCompletionResponse getProfileCompletion(Long userId) {
//
//	    User user = userRepository.findById(userId)
//	            .orElseThrow(() ->
//	                new ResourceNotFoundException(
//	                    "User not found with ID: " + userId));
//
//	    if (user.getRole() != Role.JOB_SEEKER) {
//	        throw new BadRequestException(
//	            "Profile completion is available only for job seekers");
//	    }
//
//	    List<String> missingFields = new ArrayList<>();
//
//	    if (user.getFullName() == null || user.getFullName().isBlank()) {
//	        missingFields.add("Full Name");
//	    }
//
//	    if (user.getEmail() == null || user.getEmail().isBlank()) {
//	        missingFields.add("Email");
//	    }
//
//	    if (user.getSkills() == null || user.getSkills().isEmpty()) {
//	        missingFields.add("Skills");
//	    }
//
//	    if (user.getResumes() == null || user.getResumes().isEmpty()) {
//	        missingFields.add("Resume");
//	    }
//
//	    int totalFields = 4;
//	    int completedFields = totalFields - missingFields.size();
//
//	    int percentage = (completedFields * 100) / totalFields;
//
//	    return new ProfileCompletionResponse(
//	            percentage,
//	            completedFields,
//	            totalFields,
//	            missingFields
//	    );
//	}
//
//}
