package org.myalerts.view;

import java.util.Collection;
import java.util.List;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;

@Slf4j
@AnonymousAllowed
@Route(value = StatisticsView.ROUTE, layout = BaseLayout.class)
public class StatisticsView extends ResponsiveLayout {

    public static final String ROUTE = "statistics";

    public StatisticsView(final List<CacheManager> cacheManagers) {
        super();

        for (CacheManager cacheManager : cacheManagers) {
            final Collection<String> cacheNames = cacheManager.getCacheNames();
            for (String cacheName : cacheNames) {
                Object object = cacheManager.getCache(cacheName);
                if (!(object instanceof CaffeineCache)) {
                    continue;
                }
                CaffeineCache cache = (CaffeineCache) object;
                log.info("Cache - " + cacheName + " : " + cache.getNativeCache().stats());
            }
        }

        add(createHeader(getTranslation("statistics.page.subtitle")));
        add(createFooter());
    }

}
