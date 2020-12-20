package org.myalerts.app.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class TaskSchedulerConfig {

    private final SettingsConfig settingsConfig;

    @Bean
    public TaskScheduler taskScheduler() {
        SettingsConfig.TestScenarioScheduler testScenarioScheduler = settingsConfig.getTestScenarioScheduler();

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(testScenarioScheduler.getPoolSize());
        threadPoolTaskScheduler.setThreadNamePrefix(testScenarioScheduler.getThreadNamePrefix());
        threadPoolTaskScheduler.initialize();

        return threadPoolTaskScheduler;
    }

}
