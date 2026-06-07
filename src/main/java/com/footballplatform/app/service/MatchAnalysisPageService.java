package com.footballplatform.app.service;

import com.footballplatform.app.dto.MatchAnalysisPageDataDTO;

public interface MatchAnalysisPageService {

    MatchAnalysisPageDataDTO getMatchAnalysisPageData(Long matchId);
}
