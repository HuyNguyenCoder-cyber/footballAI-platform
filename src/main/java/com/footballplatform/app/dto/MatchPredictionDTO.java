package com.footballplatform.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class MatchPredictionDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotBlank(message = "Score prediction is required")
    @Size(max = 255, message = "Score prediction must be at most 255 characters")
    private String scorePrediction;

    @NotNull(message = "Confidence is required")
    @Min(value = 0, message = "Confidence must be at least 0")
    @Max(value = 100, message = "Confidence must be at most 100")
    private Integer confidence;

    private String matchLabel;

    private String matchStatus;
}
