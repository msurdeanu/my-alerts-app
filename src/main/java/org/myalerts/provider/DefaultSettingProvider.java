package org.myalerts.provider;

import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.Setting;

import java.util.Collections;
import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
public class DefaultSettingProvider implements SettingProvider {

    private static final String DEFAULT_VALUE_IS_PROVIDED = "Default value for setting key '{}' is provided.";

    @Override
    public List<Setting> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Integer getOrDefault(Setting.Key key, Integer defaultValue) {
        log.warn(DEFAULT_VALUE_IS_PROVIDED, key.getKey());
        return defaultValue;
    }

    @Override
    public Boolean getOrDefault(Setting.Key key, Boolean defaultValue) {
        log.warn(DEFAULT_VALUE_IS_PROVIDED, key.getKey());
        return defaultValue;
    }

    @Override
    public String getOrDefault(Setting.Key key, String defaultValue) {
        log.warn(DEFAULT_VALUE_IS_PROVIDED, key.getKey());
        return defaultValue;
    }

    @Override
    public void set(Setting.Key key, Integer toValue) {
        // Nothing to do
    }

    @Override
    public void set(Setting.Key key, Boolean toValue) {
        // Nothing to do
    }

    @Override
    public void set(Setting.Key key, String toValue) {
        // Nothing to do
    }

}
