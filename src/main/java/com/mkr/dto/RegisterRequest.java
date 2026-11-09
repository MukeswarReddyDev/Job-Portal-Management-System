
package com.mkr.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mkr.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
          message = "Password must contain 8 to 72 characters")
    private String password;

    private Role role;

    @NotEmpty(message = "Please provide at least one skill")
    private List<@NotBlank(message = "Skill name cannot be blank") String> skills;

    private MultipartFile resume;
}
