package org.myalerts.app.provider;

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.myalerts.app.model.Setting;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
public class DefaultSettingProvider implements SettingProvider {

    @Override
    public List<Setting> getAll() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Integer getOrDefault(Setting.Key key, Integer defaultValue) {
        log.warn("Default value for setting key = {} was provided.", key.getKey());
        return defaultValue;
    }

    @Override
    public Boolean getOrDefault(Setting.Key key, Boolean defaultValue) {
        log.warn("Default value for setting key = {} was provided.", key.getKey());
        return defaultValue;
    }

    @Override
    public String getOrDefault(Setting.Key key, String defaultValue) {
        log.warn("Default value for setting key = {} was provided.", key.getKey());
        return defaultValue;
    }

    @Override
    public void set(Setting.Key key, Integer toValue) {
        // Nothing to do by default
    }

    @Override
    public void set(Setting.Key key, Boolean toValue) {
        // Nothing to do by default
    }

    @Override
    public void set(Setting.Key key, String toValue) {
        // Nothing to do by default
    }

}
