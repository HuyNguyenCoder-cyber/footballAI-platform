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
public class AIInsightDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotBlank(message = "AI Insight content is required")
    private String content;

    private String matchLabel;

    private String matchStatus;
}
