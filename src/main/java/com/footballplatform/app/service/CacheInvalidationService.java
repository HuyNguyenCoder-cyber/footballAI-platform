package com.footballplatform.app.service;

public interface CacheInvalidationService {

    void evictHomePageCache();

    void evictMatchAnalysisCache(Long matchId);

    void evictAllMatchAnalysisCache();
}
