package com.footballplatform.app.service;

import com.footballplatform.app.dto.HomePageDataDTO;
import com.footballplatform.app.entity.MatchStatus;

public interface HomePageService {

    HomePageDataDTO getHomePageData(Long competitionId, MatchStatus status, String timeFilter, String keyword);
}
