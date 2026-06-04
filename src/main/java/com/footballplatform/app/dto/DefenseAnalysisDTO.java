package com.footballplatform.app.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class DefenseAnalysisDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotNull(message = "Team A goals conceded is required")
    @Min(value = 0, message = "Team A goals conceded must be at least 0")
    private Integer teamAGoalsConceded;

    @NotNull(message = "Team A goals conceded per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A goals conceded per match must be at least 0")
    private Double teamAGoalsConcededPerMatch;

    @NotNull(message = "Team A clean sheets is required")
    @Min(value = 0, message = "Team A clean sheets must be at least 0")
    private Integer teamACleanSheets;

    @NotNull(message = "Team A clean sheet rate is required")
    @Min(value = 0, message = "Team A clean sheet rate must be at least 0")
    @Max(value = 100, message = "Team A clean sheet rate must be at most 100")
    private Integer teamACleanSheetRate;

    @NotNull(message = "Team A conceding rate is required")
    @Min(value = 0, message = "Team A conceding rate must be at least 0")
    @Max(value = 100, message = "Team A conceding rate must be at most 100")
    private Integer teamAConcedingRate;

    @NotNull(message = "Team A xGA per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A xGA per match must be at least 0")
    private Double teamAXgaPerMatch;

    @NotNull(message = "Team A shots conceded per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A shots conceded per match must be at least 0")
    private Double teamAShotsConcededPerMatch;

    @NotNull(message = "Team A defence index is required")
    @Min(value = 0, message = "Team A defence index must be at least 0")
    @Max(value = 100, message = "Team A defence index must be at most 100")
    private Integer teamADefenceIndex;

    @NotNull(message = "Team B goals conceded is required")
    @Min(value = 0, message = "Team B goals conceded must be at least 0")
    private Integer teamBGoalsConceded;

    @NotNull(message = "Team B goals conceded per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B goals conceded per match must be at least 0")
    private Double teamBGoalsConcededPerMatch;

    @NotNull(message = "Team B clean sheets is required")
    @Min(value = 0, message = "Team B clean sheets must be at least 0")
    private Integer teamBCleanSheets;

    @NotNull(message = "Team B clean sheet rate is required")
    @Min(value = 0, message = "Team B clean sheet rate must be at least 0")
    @Max(value = 100, message = "Team B clean sheet rate must be at most 100")
    private Integer teamBCleanSheetRate;

    @NotNull(message = "Team B conceding rate is required")
    @Min(value = 0, message = "Team B conceding rate must be at least 0")
    @Max(value = 100, message = "Team B conceding rate must be at most 100")
    private Integer teamBConcedingRate;

    @NotNull(message = "Team B xGA per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B xGA per match must be at least 0")
    private Double teamBXgaPerMatch;

    @NotNull(message = "Team B shots conceded per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B shots conceded per match must be at least 0")
    private Double teamBShotsConcededPerMatch;

    @NotNull(message = "Team B defence index is required")
    @Min(value = 0, message = "Team B defence index must be at least 0")
    @Max(value = 100, message = "Team B defence index must be at most 100")
    private Integer teamBDefenceIndex;

    @NotBlank(message = "Analysis is required")
    private String analysis;

    private String matchLabel;

    private String matchStatus;
}
