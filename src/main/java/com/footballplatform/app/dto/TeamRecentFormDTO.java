package com.footballplatform.app.dto;

import com.footballplatform.app.entity.TeamSide;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRecentFormDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotNull(message = "Team side is required")
    private TeamSide teamSide;

    @NotBlank(message = "Recent matches text is required")
    private String recentMatchesText;

    @NotNull(message = "Wins is required")
    @Min(value = 0, message = "Wins must be at least 0")
    private Integer wins;

    @NotNull(message = "Draws is required")
    @Min(value = 0, message = "Draws must be at least 0")
    private Integer draws;

    @NotNull(message = "Losses is required")
    @Min(value = 0, message = "Losses must be at least 0")
    private Integer losses;

    @NotNull(message = "Goals scored is required")
    @Min(value = 0, message = "Goals scored must be at least 0")
    private Integer goalsScored;

    @NotNull(message = "Goals conceded is required")
    @Min(value = 0, message = "Goals conceded must be at least 0")
    private Integer goalsConceded;

    @NotNull(message = "Clean sheets is required")
    @Min(value = 0, message = "Clean sheets must be at least 0")
    private Integer cleanSheets;

    @NotNull(message = "Win rate is required")
    @Min(value = 0, message = "Win rate must be at least 0")
    @Max(value = 100, message = "Win rate must be at most 100")
    private Integer winRate;

    @NotNull(message = "Clean sheet rate is required")
    @Min(value = 0, message = "Clean sheet rate must be at least 0")
    @Max(value = 100, message = "Clean sheet rate must be at most 100")
    private Integer cleanSheetRate;

    @NotNull(message = "Scoring rate is required")
    @Min(value = 0, message = "Scoring rate must be at least 0")
    @Max(value = 100, message = "Scoring rate must be at most 100")
    private Integer scoringRate;

    @NotNull(message = "Conceding rate is required")
    @Min(value = 0, message = "Conceding rate must be at least 0")
    @Max(value = 100, message = "Conceding rate must be at most 100")
    private Integer concedingRate;

    private String matchLabel;

    private String matchStatus;
}
