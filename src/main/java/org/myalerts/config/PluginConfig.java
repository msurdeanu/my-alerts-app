package org.myalerts.config;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
public class PluginConfig {

    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager();
    }

}
