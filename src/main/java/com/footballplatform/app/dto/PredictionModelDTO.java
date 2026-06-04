package com.footballplatform.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PredictionModelDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotNull(message = "Team A win probability is required")
    @Min(value = 0, message = "Probability must be at least 0")
    @Max(value = 100, message = "Probability must be at most 100")
    private Integer teamAWinProbability;

    @NotNull(message = "Draw probability is required")
    @Min(value = 0, message = "Probability must be at least 0")
    @Max(value = 100, message = "Probability must be at most 100")
    private Integer drawProbability;

    @NotNull(message = "Team B win probability is required")
    @Min(value = 0, message = "Probability must be at least 0")
    @Max(value = 100, message = "Probability must be at most 100")
    private Integer teamBWinProbability;

    @NotNull(message = "Data confidence is required")
    @Min(value = 0, message = "Data confidence must be at least 0")
    @Max(value = 100, message = "Data confidence must be at most 100")
    private Integer dataConfidence;

    private String matchLabel;

    private String matchStatus;
}
