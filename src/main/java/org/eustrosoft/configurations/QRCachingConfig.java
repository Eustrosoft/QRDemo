package org.eustrosoft.configurations;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class QRCachingConfig {
    public static final String QR_CACHE_NAME = "qrs";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(QR_CACHE_NAME);
    }
}
