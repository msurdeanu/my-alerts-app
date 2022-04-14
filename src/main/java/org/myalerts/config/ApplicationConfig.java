package org.myalerts.config;

import org.myalerts.provider.DatabaseSettingProvider;
import org.myalerts.provider.DefaultSettingProvider;
import org.myalerts.provider.SettingProvider;
import org.myalerts.repository.SettingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration("applicationConfig")
public class ApplicationConfig {

    @Bean
    public SettingProvider settingProvider(final SettingRepository settingRepository) {
        return (SettingProvider) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{SettingProvider.class},
            new DatabaseSettingProvider(new DefaultSettingProvider(), settingRepository));
    }

}
