package org.myalerts.service;

import com.vaadin.flow.data.provider.Query;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.domain.PluginFilter;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@DependsOn("pluginManager")
@RequiredArgsConstructor
public class PluginService {

    private static final Predicate<PluginWrapper> ALWAYS_TRUE_PREDICATE = pluginWrapper -> true;

    private final PluginManager pluginManager;

    public Stream<PluginWrapper> getAll() {
        return getAll(new PluginFilter(), 0, Long.MAX_VALUE);
    }

    public Stream<PluginWrapper> getAll(final PluginFilter filter, final long offset, final long limit) {
        return pluginManager.getPlugins().stream()
            .filter(getPredicateByNameCriteria(filter.getByNameCriteria()))
            .skip(offset)
            .limit(limit);
    }

    public long getAllSize() {
        return getAll().count();
    }

    public long getAllSize(final PluginFilter filter) {
        return getAll(filter, 0, Long.MAX_VALUE).count();
    }

    public Stream<PluginWrapper> findBy(final Query<PluginWrapper, PluginFilter> query) {
        return query.getFilter()
            .map(filter -> getAll(filter, query.getOffset(), query.getLimit()))
            .orElseGet(this::getAll);
    }

    public int countBy(final Query<PluginWrapper, PluginFilter> query) {
        return query.getFilter()
            .map(this::getAllSize)
            .orElseGet(this::getAllSize)
            .intValue();
    }

    private Predicate<PluginWrapper> getPredicateByNameCriteria(final String byNameCriteria) {
        return StringUtils.isNotEmpty(byNameCriteria)
            ? pluginWrapper -> StringUtils.equalsIgnoreCase(pluginWrapper.getPluginId(), byNameCriteria)
            : ALWAYS_TRUE_PREDICATE;
    }

}
