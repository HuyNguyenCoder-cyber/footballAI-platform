package com.footballplatform.app.dto;

import com.footballplatform.app.entity.TeamSide;
import java.util.List;
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
public class MatchAnalysisPageDataDTO {

    private MatchDTO match;
    private MatchPredictionDTO prediction;
    private AIInsightDTO aiInsight;
    private AttackAnalysisDTO attackAnalysis;
    private DefenseAnalysisDTO defenseAnalysis;
    private List<BetRecommendationDTO> betRecommendations;
    private HeadToHeadDTO headToHead;
    private PredictionModelDTO predictionModel;
    private List<KeyPlayerDTO> keyPlayers;
    private SquadAnalysisDTO squadAnalysis;
    private TeamRecentFormDTO homeTeamForm;
    private TeamRecentFormDTO awayTeamForm;

    public TeamRecentFormDTO getTeamForm(TeamSide side) {
        return side == TeamSide.HOME ? homeTeamForm : awayTeamForm;
    }
}
