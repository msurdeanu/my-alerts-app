package org.myalerts.app.config;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new DefaultCacheManager();
    }

    public static class DefaultCacheManager extends ConcurrentMapCacheManager {

        @Override
        protected Cache createConcurrentMapCache(final String name) {
            return new ConcurrentMapCache(name, CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(100).build().asMap(), false);
        }

    }

}
