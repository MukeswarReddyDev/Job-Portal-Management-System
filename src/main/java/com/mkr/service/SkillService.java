
package com.mkr.service;

import java.util.List;

import com.mkr.dto.SkillRequest;
import com.mkr.dto.SkillResponse;

public interface SkillService {

    SkillResponse createSkill(SkillRequest request);

    List<SkillResponse> getAllSkills();

    SkillResponse getSkillById(Long id);
    
    List<SkillResponse> createSkills(List<SkillRequest> requests);
}
