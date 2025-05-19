package org.myalerts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration("executorConfig")
@EnableScheduling
public class ExecutorConfig {

    @Bean(name = "alert-scheduler-pool")
    public TaskScheduler taskSchedulerPool() {
        final var virtualThreadFactory = Thread.ofVirtual().factory();
        final var virtualThreadScheduler = Executors.newScheduledThreadPool(1, virtualThreadFactory);
        return new ConcurrentTaskScheduler(virtualThreadScheduler);
    }

    @Bean(name = "alert-pool")
    public ExecutorService workflowPool() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
