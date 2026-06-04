package com.footballplatform.app.dto;

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
public class SquadAnalysisDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotBlank(message = "Team A content is required")
    private String teamAContent;

    @NotBlank(message = "Team B content is required")
    private String teamBContent;

    private String matchLabel;

    private String matchStatus;
}
