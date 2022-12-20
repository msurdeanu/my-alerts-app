package org.myalerts.service;

import com.vaadin.flow.data.provider.Query;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.ApplicationContext;
import org.myalerts.domain.StatisticsGroup;
import org.myalerts.domain.StatisticsItem;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioFilter;
import org.myalerts.domain.TestScenarioType;
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

import static java.util.Optional.of;
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

    private static final Map<Integer, TestScenario> ALL_TESTS = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final ApplicationContext applicationContext;

    private final ScheduleTestScenarioService scheduleTestScenarioService;

    private final TagRepository tagRepository;

    public final Set<String> getAllTags() {
        return ALL_TESTS.values().stream()
            .flatMap(testScenario -> testScenario.getTagsAsString().stream())
            .collect(Collectors.toSet());
    }

    public Stream<TestScenario> getAll() {
        return getAll(new TestScenarioFilter(), 0, Long.MAX_VALUE);
    }

    public Stream<TestScenario> getAll(final TestScenarioFilter filter, final long offset, final long limit) {
        return ALL_TESTS.values().stream()
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
        return ofNullable(ALL_TESTS.get(id));
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
            testScenario.setApplicationContext(applicationContext);

            ofNullable(ALL_TESTS.put(testScenario.getId(), testScenario))
                .filter(TestScenario::isEnabled)
                .ifPresent(scheduleTestScenarioService::unschedule);

            of(testScenario)
                .filter(TestScenario::isEnabled)
                .ifPresent(scheduleTestScenarioService::schedule);
        } finally {
            lock.unlock();
        }
    }

    public void changeActivation(@NonNull final TestScenario testScenario) {
        lock.lock();
        try {
            of(testScenario)
                .filter(TestScenario::isEnabled)
                .ifPresentOrElse(scheduleTestScenarioService::unschedule, () -> scheduleTestScenarioService.schedule(testScenario));
        } finally {
            lock.unlock();
        }

        testScenario.toggleOnEnabling();
    }

    public boolean changeDefinition(@NonNull final TestScenario testScenario, final String newDefinition) {
        lock.lock();
        try {
            return testScenario.setScript(newDefinition);
        } finally {
            lock.unlock();
        }
    }

    public boolean changeCronExpression(final TestScenario testScenario, final String newCronExpression) {
        lock.lock();
        try {
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unschedule(testScenario);
            }
            final var isCronReset = testScenario.setCron(newCronExpression);
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.schedule(testScenario);
            }
            return isCronReset;
        } finally {
            lock.unlock();
        }
    }

    public boolean changeName(final TestScenario testScenario, final String newName) {
        lock.lock();
        try {
            return testScenario.setName(newName);
        } finally {
            lock.unlock();
        }
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
            return isRemoved || isAdded;
        } finally {
            lock.unlock();
        }
    }

    public void delete(final TestScenario testScenario) {
        lock.lock();
        try {
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unschedule(testScenario);
            }

            ALL_TESTS.remove(testScenario.getId());
            testScenario.markAsDeleted();
        } finally {
            lock.unlock();
        }
    }

    public void scheduleAllNowInAsyncMode() {
        ALL_TESTS.values().stream()
            .filter(TestScenario::isEnabled)
            .forEach(scheduleTestScenarioService::scheduleInAsyncMode);
    }

    public void scheduleNowInSyncMode(final TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        scheduleTestScenarioService.scheduleInSyncMode(testScenario);
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
            ? testScenario -> testScenario.getTags().containsAll(byTagCriteria)
            : ALWAYS_TRUE_PREDICATE;
    }

}
