package org.myalerts.provider;

import org.myalerts.domain.Setting;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingProvider {

    List<Setting> getAll();

    <T> T getOrDefault(String key, T defaultValue);

    <T> void set(String key, T toValue);

}
