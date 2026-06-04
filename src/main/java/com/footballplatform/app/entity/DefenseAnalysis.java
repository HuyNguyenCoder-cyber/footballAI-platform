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
@Table(name = "defense_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefenseAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    private Integer teamAGoalsConceded;

    private Double teamAGoalsConcededPerMatch;

    private Integer teamACleanSheets;

    private Integer teamACleanSheetRate;

    private Integer teamAConcedingRate;

    private Double teamAXgaPerMatch;

    private Double teamAShotsConcededPerMatch;

    private Integer teamADefenceIndex;

    private Integer teamBGoalsConceded;

    private Double teamBGoalsConcededPerMatch;

    private Integer teamBCleanSheets;

    private Integer teamBCleanSheetRate;

    private Integer teamBConcedingRate;

    private Double teamBXgaPerMatch;

    private Double teamBShotsConcededPerMatch;

    private Integer teamBDefenceIndex;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String analysis;
}
