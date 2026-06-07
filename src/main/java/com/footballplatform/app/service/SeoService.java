package com.footballplatform.app.service;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.SeoMetaDTO;

public interface SeoService {

    SeoMetaDTO buildHomeSeo();

    SeoMetaDTO buildDonateSeo();

    SeoMetaDTO buildMatchAnalysisSeo(MatchDTO match);

    SeoMetaDTO buildKeyPlayerSeo(KeyPlayerDTO keyPlayer, MatchDTO match);

    String absoluteUrl(String path);

    String defaultImageUrl();
}
