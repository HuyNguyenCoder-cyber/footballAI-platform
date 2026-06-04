package com.footballplatform.app.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class HeadToHeadDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotBlank(message = "H2H matches text is required")
    private String h2hMatchesText;

    @NotNull(message = "Total matches is required")
    private Integer totalMatches;

    @NotNull(message = "Team A wins is required")
    private Integer teamAWins;

    @NotNull(message = "Draws is required")
    private Integer draws;

    @NotNull(message = "Team B wins is required")
    private Integer teamBWins;

    @NotNull(message = "Team A goals is required")
    private Integer teamAGoals;

    @NotNull(message = "Team B goals is required")
    private Integer teamBGoals;

    @NotNull(message = "Total goals is required")
    private Integer totalGoals;

    @NotNull(message = "Average goals per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Average goals per match must be at least 0")
    private Double averageGoalsPerMatch;

    @NotNull(message = "Team A clean sheets is required")
    private Integer teamACleanSheets;

    @NotNull(message = "Team B clean sheets is required")
    private Integer teamBCleanSheets;

    @NotBlank(message = "Analysis is required")
    private String analysis;

    private String matchLabel;

    private String matchStatus;
}
