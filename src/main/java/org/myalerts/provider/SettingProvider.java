package org.myalerts.provider;

import java.util.List;

import org.myalerts.model.Setting;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingProvider {

    List<Setting> getAll();

    Integer getOrDefault(Setting.Key key, Integer defaultValue);

    Boolean getOrDefault(Setting.Key key, Boolean defaultValue);

    String getOrDefault(Setting.Key key, String defaultValue);

    void set(Setting.Key key, Integer toValue);

    void set(Setting.Key key, Boolean toValue);

    void set(Setting.Key key, String toValue);

}
