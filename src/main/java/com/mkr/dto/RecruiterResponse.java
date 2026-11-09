
package com.mkr.dto;

import java.time.LocalDateTime;

import com.mkr.enums.RecruiterStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterResponse {

    private Long id;

    private Long userId;

    private String fullName;

    private String email;

    private String companyName;

    private String designation;

    private String phoneNumber;

    private RecruiterStatus recruiterStatus;

    private LocalDateTime createdAt;
}
