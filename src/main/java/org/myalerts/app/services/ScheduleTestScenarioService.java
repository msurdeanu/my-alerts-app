package org.myalerts.app.services;

import lombok.extern.slf4j.Slf4j;
import org.myalerts.app.interfaces.markers.ThreadSafe;
import org.myalerts.app.models.TestScenario;
import org.springframework.scheduling.TaskScheduler;
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

@Slf4j
@Service
public class ScheduleTestScenarioService {

    private final static Map<String, ScheduledFuture<?>> SCENARIOS_SCHEDULED_MAP = new HashMap<>();

    private final TaskScheduler taskScheduler;

    public ScheduleTestScenarioService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @ThreadSafe
    public void add(TestScenario testScenario) {
        final String testScenarioId = testScenario.getId();
        final String testScenarioCronExpression = testScenario.getCronExpression();

        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(testScenario,
                new CronTrigger(testScenarioCronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));

        synchronized (SCENARIOS_SCHEDULED_MAP) {
            remove(testScenario);
            SCENARIOS_SCHEDULED_MAP.put(testScenarioId, scheduledFuture);
        }

        log.info("A new test scenario {} was added to scheduling pool. Running frequency is {}", testScenarioId,
                testScenarioCronExpression);
    }

    @ThreadSafe
    public void remove(TestScenario testScenario) {
        final String testScenarioId = testScenario.getId();

        synchronized (SCENARIOS_SCHEDULED_MAP) {
            ScheduledFuture<?> scheduledFuture = SCENARIOS_SCHEDULED_MAP.get(testScenarioId);
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                SCENARIOS_SCHEDULED_MAP.remove(testScenarioId);
            }
        }

        log.info("A new test scenario {} was removed from scheduling pool.", testScenarioId);
    }

    @ThreadSafe
    public void scheduleInAsyncMode(TestScenario testScenario) {
        taskScheduler.schedule(testScenario, Instant.now());
    }

    @ThreadSafe
    public void scheduleInSyncMode(TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(testScenario, Instant.now());
        scheduledFuture.get(1, TimeUnit.MINUTES);
    }

}
