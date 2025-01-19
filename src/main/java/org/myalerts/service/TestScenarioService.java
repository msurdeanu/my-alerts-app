package org.myalerts.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.myalerts.ApplicationManager;
import org.myalerts.EventBroadcaster;
import org.myalerts.domain.StatisticsGroup;
import org.myalerts.domain.StatisticsItem;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioRunnable;
import org.myalerts.domain.TestScenarioType;
import org.myalerts.domain.event.TestScenarioDeleteEvent;
import org.myalerts.domain.event.TestScenarioUpdateEvent;
import org.myalerts.domain.filter.TestScenarioFilter;
import org.myalerts.provider.StatisticsProvider;
import org.myalerts.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestScenarioService extends AbstractDataService<TestScenario, TestScenarioFilter> implements StatisticsProvider {

    private static final TestScenarioFilter DISABLED_FILTER = new TestScenarioFilter().setByTypeCriteria(TestScenarioType.DISABLED);

    private static final TestScenarioFilter FAILED_FILTER = new TestScenarioFilter().setByTypeCriteria(TestScenarioType.FAILED);

    private static final Map<Integer, TestScenarioRunnable> ALL_SCENARIOS = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final ApplicationManager applicationManager;

    private final ScheduleTestScenarioService scheduleTestScenarioService;

    @Override
    public Stream<TestScenario> getAllItems() {
        return ALL_SCENARIOS.values().stream().map(TestScenarioRunnable::getTestScenario);
    }

    @Override
    protected TestScenarioFilter createFilter() {
        return new TestScenarioFilter();
    }

    public final Set<String> getAllTags() {
        return getAllItems()
            .flatMap(testScenario -> testScenario.getTagsAsString().stream())
            .collect(Collectors.toSet());
    }

    public void createAndSchedule(@NonNull TestScenario testScenario) {
        lock.lock();
        try {
            final var testScenarioRunnable = new TestScenarioRunnable(applicationManager, testScenario);
            ofNullable(ALL_SCENARIOS.put(testScenario.getId(), testScenarioRunnable))
                .filter(oldTestScenarioRunnable -> oldTestScenarioRunnable.getTestScenario().isEnabled())
                .ifPresent(scheduleTestScenarioService::unSchedule);
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.schedule(testScenarioRunnable);
            }
        } finally {
            lock.unlock();
        }
    }

    public void changeActivation(@NonNull TestScenario testScenario) {
        lock.lock();
        try {
            testScenario.toggleOnEnabling();

            ofNullable(ALL_SCENARIOS.get(testScenario.getId())).ifPresent(testScenarioRunnable -> {
                if (testScenarioRunnable.getTestScenario().isEnabled()) {
                    scheduleTestScenarioService.unSchedule(testScenarioRunnable);
                } else {
                    scheduleTestScenarioService.schedule(testScenarioRunnable);
                }
            });
        } finally {
            lock.unlock();
        }

        applicationManager.getBeanOfType(EventBroadcaster.class)
            .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
    }

    public boolean changeCronExpression(TestScenario testScenario, String newCronExpression) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            final var testScenarioRunnable = ALL_SCENARIOS.get(testScenario.getId());
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unSchedule(testScenarioRunnable);
            }
            isOperationPerformed = testScenario.setCron(newCronExpression);
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.schedule(testScenarioRunnable);
            }
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getBeanOfType(EventBroadcaster.class)
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeDefinition(@NonNull TestScenario testScenario, String newDefinition) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            isOperationPerformed = testScenario.setScript(newDefinition);
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getBeanOfType(EventBroadcaster.class)
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeName(TestScenario testScenario, String newName) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            isOperationPerformed = testScenario.setName(newName);
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getBeanOfType(EventBroadcaster.class)
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeTags(TestScenario testScenario, Set<String> newTags) {
        final var newTagsTrimmed = newTags.stream().map(String::trim).collect(Collectors.toSet());

        lock.lock();
        try {
            final var testScenarioTagsAsString = testScenario.getTagsAsString();
            if (newTagsTrimmed.size() == testScenarioTagsAsString.size()
                && newTagsTrimmed.containsAll(testScenarioTagsAsString)) {
                return false;
            }

            final var isRemoved = testScenario.removeTagIf(tag -> !newTagsTrimmed.contains(tag.getName()));
            final var isAdded = testScenario.addTags(newTagsTrimmed.stream()
                .filter(item -> !testScenarioTagsAsString.contains(item))
                .map(item -> applicationManager.getBeanOfType(TagRepository.class).getOrCreate(item))
                .collect(Collectors.toSet()));
            if (isRemoved || isAdded) {
                applicationManager.getBeanOfType(EventBroadcaster.class)
                    .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
            }
            return isRemoved || isAdded;
        } finally {
            lock.unlock();
        }
    }

    public void delete(TestScenario testScenario) {
        lock.lock();
        try {
            final var testScenarioRunnable = ALL_SCENARIOS.remove(testScenario.getId());
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unSchedule(testScenarioRunnable);
            }
            applicationManager.getBeanOfType(EventBroadcaster.class)
                .broadcast(TestScenarioDeleteEvent.builder().testScenario(testScenario).build());
        } finally {
            lock.unlock();
        }
    }

    public void scheduleAllNowInAsyncMode() {
        ALL_SCENARIOS.values().stream()
            .filter(testScenarioRunnable -> testScenarioRunnable.getTestScenario().isEnabled())
            .forEach(scheduleTestScenarioService::scheduleInAsyncMode);
    }

    public void scheduleNowInSyncMode(TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        scheduleTestScenarioService.scheduleInSyncMode(ALL_SCENARIOS.get(testScenario.getId()));
    }

    @Override
    public StatisticsGroup getStatisticsGroup() {
        return StatisticsGroup.builder()
            .root(StatisticsItem.builder()
                .name("statistics.test-scenarios.group")
                .icon("vaadin:folder-o")
                .build())
            .leafs(List.of(
                StatisticsItem.builder()
                    .name("statistics.test-scenarios.group.total-scenarios.name")
                    .icon("vaadin:file-text-o")
                    .value(getAllSize())
                    .description("statistics.test-scenarios.group.total-scenarios.description")
                    .build(),
                StatisticsItem.builder()
                    .name("statistics.test-scenarios.group.total-failed-scenarios.name")
                    .icon("vaadin:file-text-o")
                    .value(getAllSize(FAILED_FILTER))
                    .description("statistics.test-scenarios.group.total-failed-scenarios.description")
                    .build(),
                StatisticsItem.builder()
                    .name("statistics.test-scenarios.group.total-disabled-scenarios.name")
                    .icon("vaadin:file-text-o")
                    .value(getAllSize(DISABLED_FILTER))
                    .description("statistics.test-scenarios.group.total-disabled-scenarios.description")
                    .build()
            ))
            .build();
    }

}
