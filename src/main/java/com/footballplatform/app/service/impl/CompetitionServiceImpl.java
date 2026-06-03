package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.entity.Competition;
import com.footballplatform.app.repository.CompetitionRepository;
import com.footballplatform.app.service.CompetitionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
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
        Competition competition = new Competition();
        competition.setName(dto.getName().trim());
        return toDto(competitionRepository.save(competition));
    }

    @Override
    public CompetitionDTO update(CompetitionDTO dto) {
        Competition competition = competitionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + dto.getId()));

        competition.setName(dto.getName().trim());
        return toDto(competitionRepository.save(competition));
    }

    @Override
    public void delete(Long id) {
        if (!competitionRepository.existsById(id)) {
            throw new RuntimeException("Competition not found with id: " + id);
        }
        competitionRepository.deleteById(id);
    }

    private CompetitionDTO toDto(Competition competition) {
        return CompetitionDTO.builder()
                .id(competition.getId())
                .name(competition.getName())
                .build();
    }
}
