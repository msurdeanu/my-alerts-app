package org.myalerts.service;

import com.vaadin.flow.data.provider.Query;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.ApplicationManager;
import org.myalerts.domain.StatisticsGroup;
import org.myalerts.domain.StatisticsItem;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioFilter;
import org.myalerts.domain.TestScenarioRunnable;
import org.myalerts.domain.TestScenarioType;
import org.myalerts.domain.event.TestScenarioDeleteEvent;
import org.myalerts.domain.event.TestScenarioUpdateEvent;
import org.myalerts.provider.StatisticsProvider;
import org.myalerts.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestScenarioService implements StatisticsProvider {

    private static final Predicate<TestScenario> ALWAYS_TRUE_PREDICATE = testScenario -> true;

    private static final TestScenarioFilter DISABLED_FILTER = new TestScenarioFilter().setByTypeCriteria(TestScenarioType.DISABLED);

    private static final TestScenarioFilter FAILED_FILTER = new TestScenarioFilter().setByTypeCriteria(TestScenarioType.FAILED);

    private static final Map<Integer, TestScenarioRunnable> ALL_SCENARIOS = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final ApplicationManager applicationManager;

    private final ScheduleTestScenarioService scheduleTestScenarioService;

    private final TagRepository tagRepository;

    public final Set<String> getAllTags() {
        return ALL_SCENARIOS.values().stream()
            .map(TestScenarioRunnable::getTestScenario)
            .flatMap(testScenario -> testScenario.getTagsAsString().stream())
            .collect(Collectors.toSet());
    }

    public Stream<TestScenario> getAll() {
        return getAll(new TestScenarioFilter(), 0, Long.MAX_VALUE);
    }

    public Stream<TestScenario> getAll(final TestScenarioFilter filter, final long offset, final long limit) {
        return ALL_SCENARIOS.values().stream()
            .map(TestScenarioRunnable::getTestScenario)
            .filter(filter.getByTypeCriteria().getFilter())
            .filter(getPredicateByTagCriteria(filter.getByTagCriteria()))
            .filter(getPredicateByNameCriteria(filter.getByNameCriteria()))
            .skip(offset)
            .limit(limit);
    }

    public long getAllSize() {
        return getAll().count();
    }

    public long getAllSize(final TestScenarioFilter filter) {
        return getAll(filter, 0, Long.MAX_VALUE).count();
    }

    public Optional<TestScenario> findBy(final int id) {
        return ofNullable(ALL_SCENARIOS.get(id)).map(TestScenarioRunnable::getTestScenario);
    }

    public Stream<TestScenario> findBy(final Query<TestScenario, TestScenarioFilter> query) {
        return query.getFilter()
            .map(filter -> getAll(filter, query.getOffset(), query.getLimit()))
            .orElseGet(this::getAll);
    }

    public int countBy(final Query<TestScenario, TestScenarioFilter> query) {
        return query.getFilter()
            .map(this::getAllSize)
            .orElseGet(this::getAllSize)
            .intValue();
    }

    public void createAndSchedule(@NonNull final TestScenario testScenario) {
        lock.lock();
        try {
            final var testScenarioRunnable = new TestScenarioRunnable(applicationManager, testScenario);
            ofNullable(ALL_SCENARIOS.put(testScenario.getId(), testScenarioRunnable))
                .filter(oldTestScenarioRunnable -> oldTestScenarioRunnable.getTestScenario().isEnabled())
                .ifPresent(scheduleTestScenarioService::unschedule);
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.schedule(testScenarioRunnable);
            }
        } finally {
            lock.unlock();
        }
    }

    public void changeActivation(@NonNull final TestScenario testScenario) {
        lock.lock();
        try {
            testScenario.toggleOnEnabling();

            ofNullable(ALL_SCENARIOS.get(testScenario.getId())).ifPresent(testScenarioRunnable -> {
                if (testScenarioRunnable.getTestScenario().isEnabled()) {
                    scheduleTestScenarioService.unschedule(testScenarioRunnable);
                } else {
                    scheduleTestScenarioService.schedule(testScenarioRunnable);
                }
            });
        } finally {
            lock.unlock();
        }

        applicationManager.getEventBroadcaster()
            .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
    }

    public boolean changeCronExpression(final TestScenario testScenario, final String newCronExpression) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            final var testScenarioRunnable = ALL_SCENARIOS.get(testScenario.getId());
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unschedule(testScenarioRunnable);
            }
            isOperationPerformed = testScenario.setCron(newCronExpression);
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.schedule(testScenarioRunnable);
            }
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getEventBroadcaster()
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeDefinition(@NonNull final TestScenario testScenario, final String newDefinition) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            isOperationPerformed = testScenario.setScript(newDefinition);
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getEventBroadcaster()
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeName(final TestScenario testScenario, final String newName) {
        var isOperationPerformed = false;

        lock.lock();
        try {
            isOperationPerformed = testScenario.setName(newName);
        } finally {
            lock.unlock();
        }

        if (isOperationPerformed) {
            applicationManager.getEventBroadcaster()
                .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
        }
        return isOperationPerformed;
    }

    public boolean changeTags(final TestScenario testScenario, final Set<String> newTags) {
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
                .map(tagRepository::getOrCreate)
                .collect(Collectors.toSet()));
            if (isRemoved || isAdded) {
                applicationManager.getEventBroadcaster()
                    .broadcast(TestScenarioUpdateEvent.builder().testScenario(testScenario).build());
            }
            return isRemoved || isAdded;
        } finally {
            lock.unlock();
        }
    }

    public void delete(final TestScenario testScenario) {
        lock.lock();
        try {
            final var testScenarioRunnable = ALL_SCENARIOS.remove(testScenario.getId());
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unschedule(testScenarioRunnable);
            }
            applicationManager.getEventBroadcaster()
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

    public void scheduleNowInSyncMode(final TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
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

    private Predicate<TestScenario> getPredicateByNameCriteria(final String byNameCriteria) {
        return StringUtils.isNotEmpty(byNameCriteria)
            ? testScenario -> StringUtils.containsIgnoreCase(testScenario.getName(), byNameCriteria)
            : ALWAYS_TRUE_PREDICATE;
    }

    private Predicate<TestScenario> getPredicateByTagCriteria(final Set<String> byTagCriteria) {
        return !byTagCriteria.isEmpty()
            ? testScenario -> testScenario.getTagsAsString().containsAll(byTagCriteria)
            : ALWAYS_TRUE_PREDICATE;
    }

}
