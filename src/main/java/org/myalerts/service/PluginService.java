package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import org.myalerts.domain.filter.PluginWrapperFilter;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@DependsOn("pluginManager")
@RequiredArgsConstructor
public class PluginService extends AbstractDataService<PluginWrapper, PluginWrapperFilter> {

    private final PluginManager pluginManager;

    @Override
    public Stream<PluginWrapper> getAllItems() {
        return pluginManager.getPlugins().stream();
    }

    @Override
    protected PluginWrapperFilter createFilter() {
        return new PluginWrapperFilter();
    }

}
