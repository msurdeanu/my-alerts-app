package org.myalerts.app.configs;

import lombok.Getter;
import org.myalerts.app.interfaces.DisplaySetting;
import org.myalerts.app.interfaces.HasSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.annotation.Validated;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "settings.test-scenario-scheduler")
@Validated
@Order(10)
public class TestScenarioSchedulerConfig implements HasSettings {

    @Getter
    @DisplaySetting(
            type = DisplaySetting.Type.INTEGER,
            label = "Test Scenario - Pool Size",
            helper = "Defines total number of threads used to run scenarios periodically."
    )
    private final int poolSize = 2;

    @Getter
    @DisplaySetting(
            type = DisplaySetting.Type.TEXT,
            label = "Test Scenario - Thread Name Prefix",
            helper = "Defines a prefix for thread names to identify them easily."
    )
    private final String threadNamePrefix = "test-scenario-pool-";

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskScheduler.initialize();

        return threadPoolTaskScheduler;
    }

}
