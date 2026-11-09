
package com.mkr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mkr.dto.SkillRequest;
import com.mkr.dto.SkillResponse;
import com.mkr.service.SkillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // Create skill
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody SkillRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillService.createSkill(request));
    }

    // Get all skills
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {

        return ResponseEntity.ok(skillService.getAllSkills());
    }

    // Get skill by ID
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long id) {

        return ResponseEntity.ok(skillService.getSkillById(id));
    }
    

@PostMapping("/bulk")
public ResponseEntity<List<SkillResponse>> createSkills(
        @Valid @RequestBody List<@Valid SkillRequest> requests) {

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(skillService.createSkills(requests));
}

}
