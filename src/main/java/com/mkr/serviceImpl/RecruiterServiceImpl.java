
package com.mkr.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mkr.dto.RecruiterResponse;
import com.mkr.dto.RegisterRecruiterRequest;
import com.mkr.enums.RecruiterStatus;
import com.mkr.enums.Role;
import com.mkr.exception.BadRequestException;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.RecruiterProfile;
import com.mkr.model.User;
import com.mkr.repository.RecruiterProfileRepository;
import com.mkr.repository.UserRepository;
import com.mkr.service.RecruiterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Recruiter registration
    @Override
    @Transactional
    public RecruiterResponse registerRecruiter(RegisterRecruiterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.RECRUITER);

        RecruiterProfile profile = new RecruiterProfile();

        profile.setCompanyName(request.getCompanyName().trim());
        profile.setDesignation(request.getDesignation().trim());
        profile.setPhoneNumber(request.getPhoneNumber().trim());
        profile.setRecruiterStatus(RecruiterStatus.PENDING);

        // Maintain both sides of the relationship
        profile.setUser(user);
        user.setRecruiterProfile(profile);

        userRepository.save(user);

        return mapToRecruiterResponse(profile);
    }

    // 2. Get recruiter details using User ID
    @Override
    @Transactional(readOnly = true)
    public RecruiterResponse getRecruiterByUserId(Long userId) {

        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recruiter profile not found for user ID: " + userId));

        return mapToRecruiterResponse(profile);
    }

    // 3. Get recruiters by approval status
    @Override
    @Transactional(readOnly = true)
    public List<RecruiterResponse> getRecruitersByStatus(RecruiterStatus status) {

        return recruiterProfileRepository.findByRecruiterStatus(status)
                .stream()
                .map(this::mapToRecruiterResponse)
                .collect(Collectors.toList());
    }

    // 4. Approve recruiter
    @Override
    @Transactional
    public RecruiterResponse approveRecruiter(Long userId) {

        RecruiterProfile profile = getProfileOrThrow(userId);

        if (profile.getRecruiterStatus() != RecruiterStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending recruiters can be approved");
        }

        profile.setRecruiterStatus(RecruiterStatus.APPROVED);

        return mapToRecruiterResponse(
                recruiterProfileRepository.save(profile));
    }

    // 5. Reject recruiter
    @Override
    @Transactional
    public RecruiterResponse rejectRecruiter(Long userId) {

        RecruiterProfile profile = getProfileOrThrow(userId);

        if (profile.getRecruiterStatus() != RecruiterStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending recruiters can be rejected");
        }

        profile.setRecruiterStatus(RecruiterStatus.REJECTED);

        return mapToRecruiterResponse(
                recruiterProfileRepository.save(profile));
    }

    // Helper method to find recruiter profile
    private RecruiterProfile getProfileOrThrow(Long userId) {

        return recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recruiter profile not found for user ID: " + userId));
    }

    // Convert Entity into Response DTO
    private RecruiterResponse mapToRecruiterResponse(RecruiterProfile profile) {

        User user = profile.getUser();

        return new RecruiterResponse(
                profile.getId(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                profile.getCompanyName(),
                profile.getDesignation(),
                profile.getPhoneNumber(),
                profile.getRecruiterStatus(),
                profile.getCreatedAt()
        );
    }
}
