package org.myalerts.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.myalerts.domain.StatisticsGroup;
import org.myalerts.domain.StatisticsItem;
import org.myalerts.provider.StatisticsProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheManagerService implements StatisticsProvider {

    private final List<CacheManager> cacheManagers;

    @Override
    public StatisticsGroup getStatisticsGroup() {
        return StatisticsGroup.builder()
            .root(StatisticsItem.builder()
                .name("statistics.internal-caches.group")
                .icon("vaadin:folder-o")
                .build())
            .leafs(cacheManagers.stream()
                .flatMap(
                    cacheManager -> cacheManager.getCacheNames().stream().map(cacheName -> Pair.of(cacheName, cacheManager.getCache(cacheName))))
                .filter(pairOfCache -> pairOfCache.getRight() instanceof CaffeineCache)
                .map(pairOfCache -> StatisticsItem.builder()
                    .name("statistics.internal-caches.group." + pairOfCache.getLeft() + ".name")
                    .icon("vaadin:file-text-o")
                    .value(cacheToString(((CaffeineCache) pairOfCache.getRight()).getNativeCache()))
                    .description("statistics.internal-caches.group." + pairOfCache.getLeft() + ".description")
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    private String cacheToString(final Cache<Object, Object> cache) {
        final var cacheStats = cache.stats();
        return format("(%.4f, %d, %d)", cacheStats.hitRate(), cacheStats.totalLoadTime(), cache.estimatedSize());
    }

}
