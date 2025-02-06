package com.learning.cart;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(5)
            .removalListener(CacheConfig::LogCacheRemovalCause)
            .evictionListener(CacheConfig::LogCacheEvictionCause);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
    
    private static void LogCacheRemovalCause(Object key, Object value, RemovalCause removalCause) {
        logger.info("Removed ({}, {}). Reason: {}.", key, value, removalCause);
    }

    private static void LogCacheEvictionCause(Object key, Object value, RemovalCause removalCause) {
        logger.info("Evicted ({}, {}). Reason: {}.", key, value, removalCause);
    }
}
