package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.SettingKeyEnum;
import org.myalerts.domain.TestScenarioRunnable;
import org.myalerts.provider.SettingProvider;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTestScenarioService {

    private static final Map<Integer, ScheduledFuture<?>> SCENARIOS_SCHEDULED_MAP = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final SettingProvider settingProvider;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public void schedule(final TestScenarioRunnable testScenarioRunnable) {
        if (testScenarioRunnable == null) {
            return;
        }

        final var testScenario = testScenarioRunnable.getTestScenario();
        final var id = testScenario.getId();
        final var cron = testScenario.getCron();
        final ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(testScenarioRunnable,
            new CronTrigger("0 " + cron, TimeZone.getTimeZone(TimeZone.getDefault().getID())));

        lock.lock();
        try {
            unschedule(testScenarioRunnable);
            SCENARIOS_SCHEDULED_MAP.put(id, scheduledFuture);
        } finally {
            lock.unlock();
        }

        log.info("New test scenario '{}' added to scheduling pool. Running frequency is '{}'.", id, cron);
    }

    public void unschedule(final TestScenarioRunnable testScenarioRunnable) {
        if (testScenarioRunnable == null) {
            return;
        }

        final var id = testScenarioRunnable.getTestScenario().getId();

        lock.lock();
        try {
            ofNullable(SCENARIOS_SCHEDULED_MAP.get(id))
                .map(scheduledFuture -> scheduledFuture.cancel(true))
                .filter(isCancelled -> isCancelled)
                .ifPresent(isCancelled -> {
                    SCENARIOS_SCHEDULED_MAP.remove(id);

                    log.info("New test scenario '{}' removed from scheduling pool.", id);
                });
        } finally {
            lock.unlock();
        }
    }

    public void scheduleInAsyncMode(final TestScenarioRunnable testScenarioRunnable) {
        if (testScenarioRunnable == null) {
            return;
        }

        threadPoolTaskScheduler.schedule(testScenarioRunnable, Instant.now());
    }

    public void scheduleInSyncMode(final TestScenarioRunnable testScenarioRunnable) throws InterruptedException, ExecutionException, TimeoutException {
        if (testScenarioRunnable == null) {
            return;
        }

        threadPoolTaskScheduler.schedule(testScenarioRunnable, Instant.now())
            .get(settingProvider.getOrDefault(SettingKeyEnum.TEST_SCENARIO_EXEC_TIMEOUT, (int) TimeUnit.MINUTES.toSeconds(60)), TimeUnit.SECONDS);
    }

}
