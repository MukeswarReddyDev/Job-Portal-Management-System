
package com.mkr.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 100,
          message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Email(message = "Please enter a valid email")
    @Size(max = 150,
          message = "Email cannot exceed 150 characters")
    private String email;

    private List<String> skills;

    private MultipartFile resume;
}
