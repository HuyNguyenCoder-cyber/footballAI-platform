package com.footballplatform.app.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CompetitionDTO {

    private Long id;

    @NotBlank(message = "Competition name is required")
    @Size(max = 255, message = "Competition name must be at most 255 characters")
    private String name;
}
