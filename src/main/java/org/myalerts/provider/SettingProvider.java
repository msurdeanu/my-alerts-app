package org.myalerts.provider;

import org.myalerts.domain.Setting;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingProvider {

    List<Setting> getAll();

    Integer getOrDefault(String key, Integer defaultValue);

    Boolean getOrDefault(String key, Boolean defaultValue);

    String getOrDefault(String key, String defaultValue);

    void set(String key, Integer toValue);

    void set(String key, Boolean toValue);

    void set(String key, String toValue);

}
