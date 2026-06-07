package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.entity.Competition;
import com.footballplatform.app.repository.CompetitionRepository;
import com.footballplatform.app.service.CacheInvalidationService;
import com.footballplatform.app.service.CompetitionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                  CacheInvalidationService cacheInvalidationService) {
        this.competitionRepository = competitionRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitionDTO> findAll() {
        return competitionRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompetitionDTO findById(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + id));
        return toDto(competition);
    }

    @Override
    public CompetitionDTO create(CompetitionDTO dto) {
        String normalizedName = normalizeName(dto.getName());
        ensureNameNotDuplicate(normalizedName, null);

        Competition competition = new Competition();
        competition.setName(normalizedName);
        CompetitionDTO saved = toDto(competitionRepository.save(competition));
        cacheInvalidationService.evictHomePageCache();
        cacheInvalidationService.evictAllMatchAnalysisCache();
        return saved;
    }

    @Override
    public CompetitionDTO update(CompetitionDTO dto) {
        Competition competition = competitionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + dto.getId()));

        String normalizedName = normalizeName(dto.getName());
        ensureNameNotDuplicate(normalizedName, competition.getId());

        competition.setName(normalizedName);
        CompetitionDTO saved = toDto(competitionRepository.save(competition));
        cacheInvalidationService.evictHomePageCache();
        cacheInvalidationService.evictAllMatchAnalysisCache();
        return saved;
    }

    @Override
    public void delete(Long id) {
        if (!competitionRepository.existsById(id)) {
            throw new RuntimeException("Competition not found with id: " + id);
        }
        competitionRepository.deleteById(id);
        cacheInvalidationService.evictHomePageCache();
        cacheInvalidationService.evictAllMatchAnalysisCache();
    }

    private CompetitionDTO toDto(Competition competition) {
        return CompetitionDTO.builder()
                .id(competition.getId())
                .name(competition.getName())
                .build();
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private void ensureNameNotDuplicate(String name, Long currentId) {
        competitionRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new RuntimeException("Competition name already exists: " + name);
                    }
                });
    }
}
