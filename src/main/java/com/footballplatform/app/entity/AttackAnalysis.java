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
@Table(name = "attack_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    private Integer teamAGoals;

    private Double teamAGoalsPerMatch;

    private Integer teamAScoringRate;

    private Double teamAXgPerMatch;

    private Integer teamABigChances;

    private Double teamAShotsPerMatch;

    private Integer teamAConversionRate;

    private Integer teamAAttackIndex;

    private Integer teamBGoals;

    private Double teamBGoalsPerMatch;

    private Integer teamBScoringRate;

    private Double teamBXgPerMatch;

    private Integer teamBBigChances;

    private Double teamBShotsPerMatch;

    private Integer teamBConversionRate;

    private Integer teamBAttackIndex;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String analysis;
}
