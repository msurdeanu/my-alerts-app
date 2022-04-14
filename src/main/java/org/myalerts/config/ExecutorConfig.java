package org.myalerts.config;

import org.myalerts.domain.Setting;
import org.myalerts.provider.SettingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration("executorConfig")
@EnableScheduling
public class ExecutorConfig {

    @Bean
    public ScheduledExecutorService internalScheduler() {
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

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(final SettingProvider settingProvider) {
        return new ThreadPoolExecutor(settingProvider.getOrDefault(Setting.Key.EVENT_CORE_POOL_SIZE, 1),
            settingProvider.getOrDefault(Setting.Key.EVENT_MAX_POOL_SIZE, 2),
            30L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000), new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
