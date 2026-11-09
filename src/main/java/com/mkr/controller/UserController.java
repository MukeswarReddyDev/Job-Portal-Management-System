//

package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mkr.dto.ChangePasswordRequest;
import com.mkr.dto.ProfileCompletionResponse;
import com.mkr.dto.RegisterRequest;
import com.mkr.dto.UpdateSkillsRequest;
import com.mkr.dto.UpdateUserRequest;
import com.mkr.dto.UserResponse;
import com.mkr.enums.Role;
import com.mkr.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Public Job Seeker Registration
    @PostMapping(
        value = "/register",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> registerUser(
            @Valid @ModelAttribute RegisterRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Admin: Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 3. Admin: Get User By ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 4. Logged-in User: Get Own Profile
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.getMyProfile(authentication.getName()));
    }

    // 5. Logged-in User: Update Own Profile
    @PatchMapping(
        value = "/me",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication authentication,
            @ModelAttribute UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateUser(authentication.getName(), request));
    }

    // 6. Logged-in User: Deactivate Own Account
    @PatchMapping("/me/deactivate")
    public ResponseEntity<Void> deactivateMyAccount(
            Authentication authentication) {

        userService.deactivateUser(authentication.getName());

        return ResponseEntity.noContent().build();
    }

    // 7. Admin: Search Users
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam String keyword) {

        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    // 8. Admin: Filter Users By Role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @PathVariable Role role) {

        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // 9. Admin: Search Users By Name And Role
    @GetMapping("/search/filter")
    public ResponseEntity<List<UserResponse>> searchUsersByNameAndRole(
            @RequestParam String keyword,
            @RequestParam Role role) {

        return ResponseEntity.ok(
                userService.searchUsersByNameAndRole(keyword, role));
    }

    // 10. Logged-in User: Change Own Password
    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changeMyPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(authentication.getName(), request);

        return ResponseEntity.noContent().build();
    }

    // 11. Logged-in User: Add Skills
    @PatchMapping("/me/skills")
    public ResponseEntity<String> addMySkills(
            Authentication authentication,
            @Valid @RequestBody UpdateSkillsRequest request) {

        userService.addSkills(authentication.getName(), request);

        return ResponseEntity.ok("Skills added successfully");
    }

    // 12. Logged-in User: Remove Skills
    @PatchMapping("/me/skills/remove")
    public ResponseEntity<String> removeMySkills(
            Authentication authentication,
            @Valid @RequestBody UpdateSkillsRequest request) {

        userService.removeSkills(authentication.getName(), request);

        return ResponseEntity.ok("Skills removed successfully");
    }

    // 13. Logged-in User: Get Own Skills
    @GetMapping("/me/skills")
    public ResponseEntity<List<String>> getMySkills(
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.getUserSkills(authentication.getName()));
    }

    // 14. Logged-in Job Seeker: Get Own Profile Completion
    @GetMapping("/me/profile-completion")
    public ResponseEntity<ProfileCompletionResponse> getMyProfileCompletion(
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.getProfileCompletion(authentication.getName()));
    }
}








//package com.mkr.controller;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.mkr.dto.ChangePasswordRequest;
//import com.mkr.dto.ProfileCompletionResponse;
//import com.mkr.dto.RegisterRequest;
//import com.mkr.dto.UpdateSkillsRequest;
//import com.mkr.dto.UpdateUserRequest;
//import com.mkr.dto.UserResponse;
//import com.mkr.enums.Role;
//import com.mkr.service.UserService;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService;
//
//    // 1. User Registration API
//    @PostMapping(
//        value = "/register",
//        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<UserResponse> registerUser(
//            @Valid @ModelAttribute RegisterRequest request) {
//
//        UserResponse response = userService.registerUser(request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//    }
//
//    // 2. Get All Users
//    @GetMapping
//    public ResponseEntity<List<UserResponse>> getAllUsers() {
//
//        return ResponseEntity.ok(
//                userService.getAllUsers()
//        );
//    }
//
//    // 3. Get User By ID
//    @GetMapping("/getById/{id}")
//    public ResponseEntity<UserResponse> getUserById(
//            @PathVariable Long id) {
//
//        return ResponseEntity.ok(
//                userService.getUserById(id)
//        );
//    }
//
//    // 4. Update User Profile (Partial Update)
//    @PatchMapping(
//        value = "/update/{id}",
//        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<UserResponse> updateUser(
//            @PathVariable Long id,
//            @ModelAttribute UpdateUserRequest request) {
//
//        return ResponseEntity.ok(
//                userService.updateUser(id, request)
//        );
//    }
//
//    // 5. Deactivate User
//    @PatchMapping("/{id}/deactivate")
//    public ResponseEntity<Void> deactivateUser(
//            @PathVariable Long id) {
//
//        userService.deactivateUser(id);
//
//        return ResponseEntity.noContent().build();
//    }
//
//    // 6. Search Users
//    @GetMapping("/search")
//    public ResponseEntity<List<UserResponse>> searchUsers(
//            @RequestParam String keyword) {
//
//        return ResponseEntity.ok(
//                userService.searchUsers(keyword)
//        );
//    }
//
//    // 7. Filter Users By Role
//    @GetMapping("/role/{role}")
//    public ResponseEntity<List<UserResponse>> getUsersByRole(
//            @PathVariable Role role) {
//
//        return ResponseEntity.ok(
//                userService.getUsersByRole(role)
//        );
//    }
//
//    // 8. Search Users By Name And Role
//    @GetMapping("/search/filter")
//    public ResponseEntity<List<UserResponse>> searchUsersByNameAndRole(
//            @RequestParam String keyword,
//            @RequestParam Role role) {
//
//        return ResponseEntity.ok(
//                userService.searchUsersByNameAndRole(keyword, role)
//        );
//    }
//    
//
//	// 9. Change Password API
//	@PatchMapping("/{id}/change-password")
//	public ResponseEntity<Void> changePassword(@PathVariable Long id,
//			@Valid @RequestBody ChangePasswordRequest request) {
//
//		userService.changePassword(id, request);
//
//		return ResponseEntity.noContent().build();
// }
//	
//	
//	// to add skills 
//	@PatchMapping("/{id}/skills")
//	public ResponseEntity<String> addSkills(
//	        @PathVariable Long id,
//	        @Valid @RequestBody UpdateSkillsRequest request) {
//
//	    userService.addSkills(id, request);
//
//	    return ResponseEntity.ok("Skills added successfully");
//	}
//	
//	
//	// to remove skills 
//	@PatchMapping("/{id}/skills/remove")
//	public ResponseEntity<String> removeSkills(
//	        @PathVariable Long id,
//	        @Valid @RequestBody UpdateSkillsRequest request) {
//
//	    userService.removeSkills(id, request);
//
//	    return ResponseEntity.ok("Skills removed successfully");
//	}
//	
//	@GetMapping("/{id}/skills")
//	public ResponseEntity<List<String>> getUserSkills(
//	        @PathVariable Long id) {
//
//	    return ResponseEntity.ok(userService.getUserSkills(id));
//	}
//	
//	
//	@GetMapping("/{id}/profile-completion")
//	public ResponseEntity<ProfileCompletionResponse> getProfileCompletion(
//	        @PathVariable Long id) {
//
//	    return ResponseEntity.ok(
//	            userService.getProfileCompletion(id)
//	    );
//	}
//	
//
//}
