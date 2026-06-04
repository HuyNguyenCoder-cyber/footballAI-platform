package com.footballplatform.app.dto;

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
public class KeyPlayerDTO {

    private Long id;

    @NotNull(message = "Match is required")
    private Long matchId;

    @NotBlank(message = "Player name is required")
    private String playerName;

    @NotBlank(message = "Team name is required")
    private String teamName;

    @NotBlank(message = "Short description is required")
    private String shortDescription;

    private String imageUrl;

    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be at least 1")
    private Integer displayOrder;

    private String avatarInitials;

    private String matchLabel;

    private String matchStatus;
}
