package org.myalerts.app.configs;

import lombok.Getter;
import org.myalerts.app.interfaces.DisplaySetting;
import org.myalerts.app.interfaces.HasSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.validation.annotation.Validated;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "settings.cookie-store")
@Validated
@Order(20)
public class CookieStoreConfig implements HasSettings {

    @Getter
    @DisplaySetting(
            type = DisplaySetting.Type.INTEGER,
            label = "settings.cookie-store.expiry-time.label",
            helper = "settings.cookie-store.expiry-time.helper"
    )
    private int expiryInSeconds = 2592000;

}
