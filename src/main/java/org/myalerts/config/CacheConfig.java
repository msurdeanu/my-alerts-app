package org.myalerts.config;

import java.time.Duration;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.myalerts.model.Setting;
import org.myalerts.provider.SettingProvider;

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
    public CacheManager menuItemCacheManager(final SettingProvider settingProvider) {
        final var cacheManager = new CustomCacheManager(
            settingProvider.getOrDefault(Setting.Key.CACHE_MENU_ITEM_MAX_SIZE, 20),
            ofSeconds(settingProvider.getOrDefault(Setting.Key.CACHE_MENU_ITEM_EXPIRE_AFTER_ACCESS, 0)),
            ofSeconds(settingProvider.getOrDefault(Setting.Key.CACHE_MENU_ITEM_EXPIRE_AFTER_WRITE, 86400))
        );
        cacheManager.setCacheNames(List.of("menu-items"));
        return cacheManager;
    }

    @Bean
    public CacheManager testScenarioResultCacheManager(final SettingProvider settingProvider) {
        final var cacheManager = new CustomCacheManager(
            settingProvider.getOrDefault(Setting.Key.CACHE_TEST_SCENARIO_RESULT_MAX_SIZE, 100),
            ofSeconds(settingProvider.getOrDefault(Setting.Key.CACHE_TEST_SCENARIO_RESULT_EXPIRE_AFTER_ACCESS, 0)),
            ofSeconds(settingProvider.getOrDefault(Setting.Key.CACHE_TEST_SCENARIO_RESULT_EXPIRE_AFTER_WRITE, 300))
        );
        cacheManager.setCacheNames(List.of("test-scenario-results"));
        return cacheManager;
    }

    private static class CustomCacheManager extends CaffeineCacheManager {

        public CustomCacheManager(final long maxSize, @NotNull final Duration expireAfterAccess, @NotNull final Duration expireAfterWrite) {
            final var caffeine = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .recordStats();

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
