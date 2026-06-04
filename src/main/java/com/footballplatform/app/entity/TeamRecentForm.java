package com.footballplatform.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team_recent_forms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRecentForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Enumerated(EnumType.STRING)
    private TeamSide teamSide;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String recentMatchesText;

    private Integer wins;

    private Integer draws;

    private Integer losses;

    private Integer goalsScored;

    private Integer goalsConceded;

    private Integer cleanSheets;

    private Integer winRate;

    private Integer cleanSheetRate;

    private Integer scoringRate;

    private Integer concedingRate;
}
