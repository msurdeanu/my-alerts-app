package org.myalerts.provider;

import org.myalerts.domain.Setting;
import org.myalerts.domain.SettingKeyEnum;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingProvider {

    List<Setting> getAll();

    Integer getOrDefault(SettingKeyEnum key, Integer defaultValue);

    Boolean getOrDefault(SettingKeyEnum key, Boolean defaultValue);

    String getOrDefault(SettingKeyEnum key, String defaultValue);

    void set(SettingKeyEnum key, Integer toValue);

    void set(SettingKeyEnum key, Boolean toValue);

    void set(SettingKeyEnum key, String toValue);

}
