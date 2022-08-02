package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.Setting;
import org.myalerts.domain.TestScenario;
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

    public void schedule(final TestScenario testScenario) {
        final var id = testScenario.getId();
        final var cron = testScenario.getCron();
        final ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(testScenario,
            new CronTrigger("0 " + cron, TimeZone.getTimeZone(TimeZone.getDefault().getID())));

        lock.lock();
        try {
            unschedule(testScenario);
            SCENARIOS_SCHEDULED_MAP.put(id, scheduledFuture);
        } finally {
            lock.unlock();
        }

        log.info("New test scenario '{}' added to scheduling pool. Running frequency is '{}'", id, cron);
    }

    public void unschedule(final TestScenario testScenario) {
        final var id = testScenario.getId();

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

    public void scheduleInAsyncMode(final TestScenario testScenario) {
        threadPoolTaskScheduler.schedule(testScenario, Instant.now());
    }

    public void scheduleInSyncMode(final TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        threadPoolTaskScheduler.schedule(testScenario, Instant.now())
            .get(settingProvider.getOrDefault(Setting.Key.TEST_SCENARIO_EXEC_TIMEOUT, (int) TimeUnit.MINUTES.toSeconds(60)), TimeUnit.SECONDS);
    }

}
