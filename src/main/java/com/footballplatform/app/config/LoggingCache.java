package com.footballplatform.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class LoggingCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(LoggingCache.class);

    private final Cache delegate;

    public LoggingCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = delegate.get(key);
        logLookup(key, value != null);
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = delegate.get(key, type);
        logLookup(key, value != null);
        return value;
    }

    @Override
    public <T> T get(Object key, java.util.concurrent.Callable<T> valueLoader) {
        ValueWrapper existing = delegate.get(key);
        if (existing != null) {
            logLookup(key, true);
            Object value = existing.get();
            return value == null ? null : (T) value;
        }

        logLookup(key, false);
        return delegate.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return delegate.evictIfPresent(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean invalidate() {
        return delegate.invalidate();
    }

    private void logLookup(Object key, boolean hit) {
        log.info("Cache {} - {} - key={}", hit ? "HIT" : "MISS", getName(), key);
    }
}
