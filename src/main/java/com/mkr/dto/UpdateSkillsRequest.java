package com.mkr.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UpdateSkillsRequest {

    @NotEmpty(message = "Skills list cannot be empty")
    private List<@NotBlank(message = "Skill name cannot be blank") String> skills;
}