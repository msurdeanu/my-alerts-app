package org.myalerts.config;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import org.myalerts.model.Setting;
import org.myalerts.provider.DatabaseSettingProvider;
import org.myalerts.provider.DefaultSettingProvider;
import org.myalerts.provider.SettingProvider;
import org.myalerts.repository.SettingRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration("applicationConfig")
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public SettingProvider settingProvider(final SettingRepository settingRepository) {
        return (SettingProvider) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {SettingProvider.class},
            new DatabaseSettingProvider(new DefaultSettingProvider(), settingRepository));
    }

    @Bean
    public Executor internalScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(final SettingProvider settingProvider) {
        final var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(settingProvider.getOrDefault(Setting.Key.TEST_SCENARIO_POOL_SIZE, 2));
        threadPoolTaskScheduler.setThreadNamePrefix(settingProvider.getOrDefault(Setting.Key.TEST_SCENARIO_THREAD_NAME_PREFIX, "test-scenario-pool-"));
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

}
