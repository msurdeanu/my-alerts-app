package org.myalerts.app.config;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.vaadin.flow.server.connect.VaadinEndpointProperties;
import org.myalerts.app.model.Setting;
import org.myalerts.app.provider.DatabaseSettingProvider;
import org.myalerts.app.provider.DefaultSettingProvider;
import org.myalerts.app.provider.SettingProvider;
import org.myalerts.app.repository.SettingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public SettingProvider settingProvider(SettingRepository settingRepository) {
        return (SettingProvider) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {SettingProvider.class},
            new DatabaseSettingProvider(new DefaultSettingProvider(), settingRepository));
    }

    @Bean
    public Executor internalScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public TaskScheduler taskScheduler(SettingProvider settingProvider) {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(settingProvider.getOrDefault(Setting.Key.TestScenarioPoolSize, 2));
        threadPoolTaskScheduler.setThreadNamePrefix(settingProvider.getOrDefault(Setting.Key.TestScenarioThreadNamePrefix, "test-scenario-pool-"));
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Bean
    public VaadinEndpointProperties vaadinEndpointProperties() {
        return new VaadinEndpointProperties();
    }

}
