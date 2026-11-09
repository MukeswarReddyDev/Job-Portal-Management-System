package com.mkr.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCompletionResponse {

    private int completionPercentage;
    private int completedFields;
    private int totalFields;
    private List<String> missingFields;
}