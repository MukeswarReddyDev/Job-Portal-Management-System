package com.mkr.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mkr.dto.SkillRequest;
import com.mkr.dto.SkillResponse;
import com.mkr.exception.BadRequestException;
import com.mkr.exception.DuplicateResourceException;
import com.mkr.exception.ResourceNotFoundException;
import com.mkr.model.Skill;
import com.mkr.repository.SkillRepository;
import com.mkr.service.SkillService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillServiceImpl implements SkillService {

	private final SkillRepository skillRepository;

	// Create skill
	@Override
	public SkillResponse createSkill(SkillRequest request) {

		String skillName = request.getSkillName().trim().toUpperCase(Locale.ROOT);

		if (skillRepository.existsBySkillNameIgnoreCase(skillName)) {
			throw new DuplicateResourceException("Skill already exists: " + skillName);
		}

		Skill skill = new Skill();
		skill.setSkillName(skillName);

		Skill savedSkill = skillRepository.save(skill);

		return mapToResponse(savedSkill);
	}

	// Get all skills
	@Override
	@Transactional(readOnly = true)
	public List<SkillResponse> getAllSkills() {

		return skillRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	// Get skill by ID
	@Override
	@Transactional(readOnly = true)
	public SkillResponse getSkillById(Long id) {

		Skill skill = skillRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

		return mapToResponse(skill);
	}

	// Convert entity to DTO
	private SkillResponse mapToResponse(Skill skill) {

		return new SkillResponse(skill.getId(), skill.getSkillName());
	}
	
	@Override
	@Transactional
	public List<SkillResponse> createSkills(List<SkillRequest> requests) {

	    List<SkillResponse> responses = new ArrayList<>();

	    for (SkillRequest request : requests) {

	        String skillName = request.getSkillName().trim();

	        if (skillRepository.existsBySkillNameIgnoreCase(skillName)) {
	            throw new BadRequestException(
	                    "Skill already exists: " + skillName);
	        }

	        Skill skill = new Skill();
	        skill.setSkillName(skillName);

	        Skill savedSkill = skillRepository.save(skill);

	        responses.add(mapToResponse(savedSkill));
	    }

	    return responses;
	}
}
