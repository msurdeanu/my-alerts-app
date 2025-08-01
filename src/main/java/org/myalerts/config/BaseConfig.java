package org.myalerts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Setter
@Getter
@Configuration("baseConfig")
@ConfigurationProperties(prefix = "my-alerts.config.base")
public class BaseConfig {

    private String rememberMeCookieName = "ma-rm";
    private short rememberMeCookieDays = 30;

}
