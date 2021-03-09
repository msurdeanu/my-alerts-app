package org.myalerts.app.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.app.interfaces.marker.ThreadSafe;
import org.myalerts.app.model.TestScenario;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTestScenarioService {

    private static final Map<Integer, ScheduledFuture<?>> SCENARIOS_SCHEDULED_MAP = new HashMap<>();

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final TaskScheduler taskScheduler;

    @ThreadSafe
    public void add(TestScenario testScenario) {
        final Integer id = testScenario.getId();
        final String cron = testScenario.getCron();
        final ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(testScenario,
            new CronTrigger(cron, TimeZone.getTimeZone(TimeZone.getDefault().getID())));

        reentrantLock.lock();
        try {
            remove(testScenario);
            SCENARIOS_SCHEDULED_MAP.put(id, scheduledFuture);
        } finally {
            reentrantLock.unlock();
        }

        log.info("A new test scenario {} was added to scheduling pool. Running frequency is {}", id, cron);
    }

    @ThreadSafe
    public void remove(TestScenario testScenario) {
        final Integer id = testScenario.getId();

        reentrantLock.lock();
        try {
            Optional.ofNullable(SCENARIOS_SCHEDULED_MAP.get(id))
                .ifPresent(scheduledFuture -> {
                    scheduledFuture.cancel(true);
                    SCENARIOS_SCHEDULED_MAP.remove(id);

                    log.info("A new test scenario {} was removed from scheduling pool.", id);
                });
        } finally {
            reentrantLock.unlock();
        }
    }

    @ThreadSafe
    public void scheduleInAsyncMode(TestScenario testScenario) {
        taskScheduler.schedule(testScenario, Instant.now());
    }

    @ThreadSafe
    public void scheduleInSyncMode(TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        taskScheduler.schedule(testScenario, Instant.now()).get(1, TimeUnit.MINUTES);
    }

}
