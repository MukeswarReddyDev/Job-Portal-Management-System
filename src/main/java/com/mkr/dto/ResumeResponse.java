
package com.mkr.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private boolean isPrimary;
    private LocalDateTime uploadedAt;
}
