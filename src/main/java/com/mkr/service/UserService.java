//
package com.mkr.service;

import java.util.List;

import com.mkr.dto.ChangePasswordRequest;
import com.mkr.dto.ProfileCompletionResponse;
import com.mkr.dto.RegisterRequest;
import com.mkr.dto.UpdateSkillsRequest;
import com.mkr.dto.UpdateUserRequest;
import com.mkr.dto.UserResponse;
import com.mkr.enums.Role;

public interface UserService {

    // Registration
    UserResponse registerUser(RegisterRequest request);

    // Admin operations
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    List<UserResponse> searchUsers(String keyword);

    List<UserResponse> getUsersByRole(Role role);

    List<UserResponse> searchUsersByNameAndRole(
            String keyword, Role role);

    // Logged-in user's profile
    UserResponse getMyProfile(String email);

    UserResponse updateUser(
            String email, UpdateUserRequest request);

    void deactivateUser(String email);

    // Password management
    void changePassword(
            String email, ChangePasswordRequest request);

    // Skills management
    void addSkills(
            String email, UpdateSkillsRequest request);

    void removeSkills(
            String email, UpdateSkillsRequest request);

    List<String> getUserSkills(String email);

    // Profile completion
    ProfileCompletionResponse getProfileCompletion(String email);
}






















//package com.mkr.service;
//
//import java.util.List;
//
//import com.mkr.dto.ChangePasswordRequest;
//import com.mkr.dto.ProfileCompletionResponse;
//import com.mkr.dto.RegisterRequest;
//import com.mkr.dto.UpdateSkillsRequest;
//import com.mkr.dto.UpdateUserRequest;
//import com.mkr.dto.UserResponse;
//import com.mkr.enums.Role;
//
//public interface UserService {
//
//    UserResponse registerUser(RegisterRequest request);
//    
//    List<UserResponse> getAllUsers();   
//    
//    
//
//    UserResponse getUserById(Long id);
//    
//    UserResponse updateUser(Long id, UpdateUserRequest request);
//    
//    void deactivateUser(Long id);
//    
//    List<UserResponse> searchUsers(String keyword);
//
//    List<UserResponse> getUsersByRole(Role role);
//
//    List<UserResponse> searchUsersByNameAndRole( String keyword, Role role);
//           
//    void changePassword(Long id, ChangePasswordRequest request);
//    
//    void addSkills(Long userId, UpdateSkillsRequest request);
//    
//    void removeSkills(Long userId, UpdateSkillsRequest request);
//    
//    List<String> getUserSkills(Long userId);
//    
//    ProfileCompletionResponse getProfileCompletion(Long userId);
//
//}
