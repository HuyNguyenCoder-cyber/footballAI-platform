package com.footballplatform.app.dto;

import com.footballplatform.app.entity.BetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class BetRecommendationDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotNull(message = "Bet type is required")
    private BetType betType;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Recommendation is required")
    @Size(max = 500, message = "Recommendation must be at most 500 characters")
    private String recommendation;

    @NotNull(message = "Display order is required")
    @PositiveOrZero(message = "Display order must be zero or positive")
    private Integer displayOrder;

    private String matchLabel;

    private String matchStatus;
}
