package com.footballplatform.app.config;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class LoggingCacheManager implements CacheManager {

    private final CacheManager delegate;
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    public LoggingCacheManager(CacheManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, cacheName -> {
            Cache cache = delegate.getCache(cacheName);
            return cache == null ? null : new LoggingCache(cache);
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
