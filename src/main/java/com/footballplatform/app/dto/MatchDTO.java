package com.footballplatform.app.dto;

import com.footballplatform.app.entity.MatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDTO {

    private Long id;

    @NotBlank(message = "Team A is required")
    @Size(max = 255, message = "Team A must be at most 255 characters")
    private String teamA;

    @Size(max = 255, message = "Team A logo must be at most 255 characters")
    private String teamALogo;

    private MultipartFile teamALogoFile;

    @NotBlank(message = "Team B is required")
    @Size(max = 255, message = "Team B must be at most 255 characters")
    private String teamB;

    @Size(max = 255, message = "Team B logo must be at most 255 characters")
    private String teamBLogo;

    private MultipartFile teamBLogoFile;

    @NotNull(message = "Match time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime matchTime;

    @NotNull(message = "Match status is required")
    private MatchStatus status;

    @NotNull(message = "Competition is required")
    private Long competitionId;

    private String competitionName;
}
