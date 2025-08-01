package org.myalerts.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.validation.constraints.NotNull;
import org.myalerts.provider.SettingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.List;

import static java.time.Duration.ofSeconds;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration("cacheConfig")
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager menuItemCacheManager(SettingProvider settingProvider) {
        final var cacheManager = new CustomCacheManager(
                settingProvider.getOrDefault("cacheMenuItemMaxSize", 20),
                ofSeconds(settingProvider.getOrDefault("cacheMenuItemExpireAfterAccess", 0)),
                ofSeconds(settingProvider.getOrDefault("cacheMenuItemExpireAfterWrite", 86_400))
        );
        cacheManager.setCacheNames(List.of("menu-items"));
        return cacheManager;
    }

    @Bean
    public CacheManager testScenarioResultCacheManager(SettingProvider settingProvider) {
        final var cacheManager = new CustomCacheManager(
                settingProvider.getOrDefault("cacheTestScenarioResultMaxSize", 100),
                ofSeconds(settingProvider.getOrDefault("cacheTestScenarioResultExpireAfterAccess", 300)),
                ofSeconds(settingProvider.getOrDefault("cacheTestScenarioResultExpireAfterWrite", 0))
        );
        cacheManager.setCacheNames(List.of("test-scenario-results"));
        return cacheManager;
    }

    @Bean
    public CacheManager translationKeyCacheManager(SettingProvider settingProvider) {
        final var cacheManager = new CustomCacheManager(
                settingProvider.getOrDefault("cacheTranslationKeyMaxSize", 10_000),
                ofSeconds(settingProvider.getOrDefault("cacheTranslationKeyExpireAfterAccess", 0)),
                ofSeconds(settingProvider.getOrDefault("cacheTranslationKeyExpireAfterWrite", 3_600))
        );
        cacheManager.setCacheNames(List.of("translation-keys"));
        return cacheManager;
    }

    private static class CustomCacheManager extends CaffeineCacheManager {

        public CustomCacheManager(long maxSize, @NotNull Duration expireAfterAccess, @NotNull Duration expireAfterWrite) {
            final var caffeine = Caffeine.newBuilder().maximumSize(maxSize).recordStats();
            if (expireAfterAccess.getSeconds() > 0) {
                caffeine.expireAfterAccess(expireAfterAccess);
            }
            if (expireAfterWrite.getSeconds() > 0) {
                caffeine.expireAfterWrite(expireAfterWrite);
            }
            setCaffeine(caffeine);
        }

    }

}
