package org.myalerts.config;

import org.myalerts.domain.SettingKeyEnum;
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
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(SettingProvider settingProvider) {
        final var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(settingProvider.getOrDefault(SettingKeyEnum.TEST_SCENARIO_POOL_SIZE, 2));
        threadPoolTaskScheduler.setThreadNamePrefix(settingProvider.getOrDefault(SettingKeyEnum.TEST_SCENARIO_THREAD_NAME_PREFIX, "test-scenario-pool-"));
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(SettingProvider settingProvider) {
        final var threadPoolExecutor = new ThreadPoolExecutor(settingProvider.getOrDefault(SettingKeyEnum.EVENT_CORE_POOL_SIZE, 1),
            settingProvider.getOrDefault(SettingKeyEnum.EVENT_MAX_POOL_SIZE, 2),
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000), new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

}
