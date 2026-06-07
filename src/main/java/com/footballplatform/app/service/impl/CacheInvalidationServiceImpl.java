package com.footballplatform.app.service.impl;

import com.footballplatform.app.config.CacheKeys;
import com.footballplatform.app.config.CacheNames;
import com.footballplatform.app.service.CacheInvalidationService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheInvalidationServiceImpl implements CacheInvalidationService {

    private final CacheManager cacheManager;

    public CacheInvalidationServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void evictHomePageCache() {
        invalidateCache(CacheNames.HOME_PAGE);
    }

    @Override
    public void evictMatchAnalysisCache(Long matchId) {
        if (matchId == null) {
            return;
        }

        Cache cache = cacheManager.getCache(CacheNames.MATCH_ANALYSIS);
        if (cache != null) {
            cache.evict(CacheKeys.matchAnalysis(matchId));
        }
    }

    @Override
    public void evictAllMatchAnalysisCache() {
        invalidateCache(CacheNames.MATCH_ANALYSIS);
    }

    private void invalidateCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
