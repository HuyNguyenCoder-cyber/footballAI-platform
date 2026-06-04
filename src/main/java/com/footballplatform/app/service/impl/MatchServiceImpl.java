package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.Competition;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.repository.CompetitionRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.MatchService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;
    private final Path uploadDir;

    public MatchServiceImpl(MatchRepository matchRepository,
                            CompetitionRepository competitionRepository,
                            @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.matchRepository = matchRepository;
        this.competitionRepository = competitionRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("matches");

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create upload directory: " + this.uploadDir, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> findAll() {
        return matchRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> findByStatuses(List<MatchStatus> statuses) {
        return matchRepository.findByStatusInOrderByMatchTimeAsc(statuses)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MatchDTO findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        return toDto(match);
    }

    @Override
    public MatchDTO create(MatchDTO dto) {
        Match match = new Match();
        applyDto(match, dto);
        return toDto(matchRepository.save(match));
    }

    @Override
    public MatchDTO update(MatchDTO dto) {
        Match match = matchRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getId()));
        applyDto(match, dto);
        return toDto(matchRepository.save(match));
    }

    @Override
    public void delete(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new RuntimeException("Match not found with id: " + id);
        }
        matchRepository.deleteById(id);
    }

    private void applyDto(Match match, MatchDTO dto) {
        Competition competition = competitionRepository.findById(dto.getCompetitionId())
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + dto.getCompetitionId()));

        match.setTeamA(dto.getTeamA().trim());
        match.setTeamB(dto.getTeamB().trim());
        match.setMatchTime(dto.getMatchTime());
        match.setStatus(dto.getStatus());
        match.setCompetition(competition);
        match.setTeamALogo(resolveLogoPath(dto.getTeamALogo(), dto.getTeamALogoFile()));
        match.setTeamBLogo(resolveLogoPath(dto.getTeamBLogo(), dto.getTeamBLogoFile()));
    }

    private MatchDTO toDto(Match match) {
        return MatchDTO.builder()
                .id(match.getId())
                .teamA(match.getTeamA())
                .teamALogo(match.getTeamALogo())
                .teamB(match.getTeamB())
                .teamBLogo(match.getTeamBLogo())
                .matchTime(match.getMatchTime())
                .status(match.getStatus())
                .competitionId(match.getCompetition() != null ? match.getCompetition().getId() : null)
                .competitionName(match.getCompetition() != null ? match.getCompetition().getName() : null)
                .competition(match.getCompetition() == null ? null : CompetitionDTO.builder()
                        .id(match.getCompetition().getId())
                        .name(match.getCompetition().getName())
                        .build())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolveLogoPath(String existingPath, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return saveFile(file);
        }
        return trimToNull(existingPath);
    }

    private String saveFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String cleanedName = originalFilename == null ? "logo" : originalFilename.replaceAll("[\\\\/]+", "_");
            if (cleanedName.length() > 150) {
                cleanedName = cleanedName.substring(0, 150);
            }
            String storedName = UUID.randomUUID() + "_" + cleanedName;
            Path targetPath = uploadDir.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath);
            return "/uploads/matches/" + storedName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), ex);
        }
    }
}
