package com.footballplatform.app.service.impl;

import com.footballplatform.app.config.CacheNames;
import com.footballplatform.app.dto.MatchAnalysisPageDataDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.TeamSide;
import com.footballplatform.app.service.AIInsightService;
import com.footballplatform.app.service.AttackAnalysisService;
import com.footballplatform.app.service.BetRecommendationService;
import com.footballplatform.app.service.DefenseAnalysisService;
import com.footballplatform.app.service.HeadToHeadService;
import com.footballplatform.app.service.KeyPlayerService;
import com.footballplatform.app.service.MatchAnalysisPageService;
import com.footballplatform.app.service.MatchPredictionService;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.PredictionModelService;
import com.footballplatform.app.service.SquadAnalysisService;
import com.footballplatform.app.service.TeamRecentFormService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MatchAnalysisPageServiceImpl implements MatchAnalysisPageService {

    private final MatchService matchService;
    private final MatchPredictionService matchPredictionService;
    private final AIInsightService aiInsightService;
    private final AttackAnalysisService attackAnalysisService;
    private final DefenseAnalysisService defenseAnalysisService;
    private final BetRecommendationService betRecommendationService;
    private final HeadToHeadService headToHeadService;
    private final PredictionModelService predictionModelService;
    private final KeyPlayerService keyPlayerService;
    private final SquadAnalysisService squadAnalysisService;
    private final TeamRecentFormService teamRecentFormService;

    public MatchAnalysisPageServiceImpl(MatchService matchService,
                                        MatchPredictionService matchPredictionService,
                                        AIInsightService aiInsightService,
                                        AttackAnalysisService attackAnalysisService,
                                        DefenseAnalysisService defenseAnalysisService,
                                        BetRecommendationService betRecommendationService,
                                        HeadToHeadService headToHeadService,
                                        PredictionModelService predictionModelService,
                                        KeyPlayerService keyPlayerService,
                                        SquadAnalysisService squadAnalysisService,
                                        TeamRecentFormService teamRecentFormService) {
        this.matchService = matchService;
        this.matchPredictionService = matchPredictionService;
        this.aiInsightService = aiInsightService;
        this.attackAnalysisService = attackAnalysisService;
        this.defenseAnalysisService = defenseAnalysisService;
        this.betRecommendationService = betRecommendationService;
        this.headToHeadService = headToHeadService;
        this.predictionModelService = predictionModelService;
        this.keyPlayerService = keyPlayerService;
        this.squadAnalysisService = squadAnalysisService;
        this.teamRecentFormService = teamRecentFormService;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MATCH_ANALYSIS,
            key = "T(com.footballplatform.app.config.CacheKeys).matchAnalysis(#matchId)",
            sync = true)
    public MatchAnalysisPageDataDTO getMatchAnalysisPageData(Long matchId) {
        MatchDTO match = matchService.findById(matchId);

        return MatchAnalysisPageDataDTO.builder()
                .match(match)
                .prediction(matchPredictionService.findByMatchId(matchId).orElse(null))
                .aiInsight(aiInsightService.findByMatchId(matchId).orElse(null))
                .attackAnalysis(attackAnalysisService.findByMatchId(matchId).orElse(null))
                .defenseAnalysis(defenseAnalysisService.findByMatchId(matchId).orElse(null))
                .betRecommendations(betRecommendationService.findByMatchId(matchId))
                .headToHead(headToHeadService.findByMatchId(matchId).orElse(null))
                .predictionModel(predictionModelService.findByMatchId(matchId).orElse(null))
                .keyPlayers(keyPlayerService.findByMatchId(matchId))
                .squadAnalysis(squadAnalysisService.findByMatchId(matchId).orElse(null))
                .homeTeamForm(teamRecentFormService.findByMatchIdAndTeamSide(matchId, TeamSide.HOME).orElse(null))
                .awayTeamForm(teamRecentFormService.findByMatchIdAndTeamSide(matchId, TeamSide.AWAY).orElse(null))
                .build();
    }
}
