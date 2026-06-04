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
public class AttackAnalysisDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotNull(message = "Team A goals is required")
    @Min(value = 0, message = "Team A goals must be at least 0")
    private Integer teamAGoals;

    @NotNull(message = "Team A goals per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A goals per match must be at least 0")
    private Double teamAGoalsPerMatch;

    @NotNull(message = "Team A scoring rate is required")
    @Min(value = 0, message = "Team A scoring rate must be at least 0")
    @Max(value = 100, message = "Team A scoring rate must be at most 100")
    private Integer teamAScoringRate;

    @NotNull(message = "Team A xG per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A xG per match must be at least 0")
    private Double teamAXgPerMatch;

    @NotNull(message = "Team A big chances is required")
    @Min(value = 0, message = "Team A big chances must be at least 0")
    private Integer teamABigChances;

    @NotNull(message = "Team A shots per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team A shots per match must be at least 0")
    private Double teamAShotsPerMatch;

    @NotNull(message = "Team A conversion rate is required")
    @Min(value = 0, message = "Team A conversion rate must be at least 0")
    @Max(value = 100, message = "Team A conversion rate must be at most 100")
    private Integer teamAConversionRate;

    @NotNull(message = "Team A attack index is required")
    @Min(value = 0, message = "Team A attack index must be at least 0")
    @Max(value = 100, message = "Team A attack index must be at most 100")
    private Integer teamAAttackIndex;

    @NotNull(message = "Team B goals is required")
    @Min(value = 0, message = "Team B goals must be at least 0")
    private Integer teamBGoals;

    @NotNull(message = "Team B goals per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B goals per match must be at least 0")
    private Double teamBGoalsPerMatch;

    @NotNull(message = "Team B scoring rate is required")
    @Min(value = 0, message = "Team B scoring rate must be at least 0")
    @Max(value = 100, message = "Team B scoring rate must be at most 100")
    private Integer teamBScoringRate;

    @NotNull(message = "Team B xG per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B xG per match must be at least 0")
    private Double teamBXgPerMatch;

    @NotNull(message = "Team B big chances is required")
    @Min(value = 0, message = "Team B big chances must be at least 0")
    private Integer teamBBigChances;

    @NotNull(message = "Team B shots per match is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Team B shots per match must be at least 0")
    private Double teamBShotsPerMatch;

    @NotNull(message = "Team B conversion rate is required")
    @Min(value = 0, message = "Team B conversion rate must be at least 0")
    @Max(value = 100, message = "Team B conversion rate must be at most 100")
    private Integer teamBConversionRate;

    @NotNull(message = "Team B attack index is required")
    @Min(value = 0, message = "Team B attack index must be at least 0")
    @Max(value = 100, message = "Team B attack index must be at most 100")
    private Integer teamBAttackIndex;

    @NotBlank(message = "Analysis is required")
    private String analysis;

    private String matchLabel;

    private String matchStatus;
}
