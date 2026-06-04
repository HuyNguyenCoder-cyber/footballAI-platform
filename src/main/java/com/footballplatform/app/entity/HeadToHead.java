package com.footballplatform.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "head_to_heads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeadToHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String h2hMatchesText;

    private Integer totalMatches;

    private Integer teamAWins;

    private Integer draws;

    private Integer teamBWins;

    private Integer teamAGoals;

    private Integer teamBGoals;

    private Integer totalGoals;

    private Double averageGoalsPerMatch;

    private Integer teamACleanSheets;

    private Integer teamBCleanSheets;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String analysis;
}
