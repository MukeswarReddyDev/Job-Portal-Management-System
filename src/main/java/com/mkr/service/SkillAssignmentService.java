
package com.mkr.service;

import java.util.List;

import com.mkr.dto.SkillResponse;

public interface SkillAssignmentService {

    SkillResponse addSkillToUser(Long userId, Long skillId);

    List<SkillResponse> getUserSkills(Long userId);

    void removeSkillFromUser(Long userId, Long skillId);

    SkillResponse addSkillToJob(Long jobId, Long skillId);

    List<SkillResponse> getJobSkills(Long jobId);

    void removeSkillFromJob(Long jobId, Long skillId);
}
