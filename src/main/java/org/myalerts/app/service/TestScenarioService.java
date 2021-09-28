package org.myalerts.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import org.myalerts.app.marker.ThreadSafe;
import org.myalerts.app.model.TestScenario;
import org.myalerts.app.model.TestScenarioFilter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestScenarioService {

    private static final Map<Integer, TestScenario> ALL_TESTS = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final ScheduleTestScenarioService scheduleTestScenarioService;

    public Stream<TestScenario> getAll() {
        return getAll(new TestScenarioFilter(), 0, Long.MAX_VALUE);
    }

    public Stream<TestScenario> getAll(final TestScenarioFilter filter, final long offset, final long limit) {
        return ALL_TESTS.values().stream()
            .filter(filter.getByTypeCriteria().getFilter())
            .filter(getPredicateByNameCriteria(filter.getByNameCriteria()))
            .skip(offset)
            .limit(limit);
    }

    public long getAllSize() {
        return getAll().count();
    }

    public long getAllSize(final TestScenarioFilter filter, final long offset, final long limit) {
        return getAll(filter, offset, limit).count();
    }

    public Stream<TestScenario> findBy(final Query<TestScenario, TestScenarioFilter> query) {
        return query.getFilter()
            .map(filter -> getAll(filter, query.getOffset(), query.getLimit()))
            .orElseGet(this::getAll);
    }

    public int countBy(Query<TestScenario, TestScenarioFilter> query) {
        return query.getFilter()
            .map(filter -> getAllSize(filter, query.getOffset(), query.getLimit()))
            .orElseGet(this::getAllSize)
            .intValue();
    }

    @ThreadSafe
    public void createAndSchedule(@NonNull TestScenario testScenario) {
        lock.lock();
        try {
            Optional.ofNullable(ALL_TESTS.put(testScenario.getId(), testScenario))
                .filter(TestScenario::isEnabled)
                .ifPresent(scheduleTestScenarioService::unschedule);

            Optional.of(testScenario)
                .filter(TestScenario::isEnabled)
                .ifPresent(scheduleTestScenarioService::schedule);
        } finally {
            lock.unlock();
        }
    }

    @ThreadSafe
    public void changeActivation(@NonNull TestScenario testScenario) {
        Optional.of(testScenario)
            .filter(TestScenario::isEnabled)
            .ifPresentOrElse(scheduleTestScenarioService::unschedule, () -> scheduleTestScenarioService.schedule(testScenario));

        testScenario.toggleOnEnabling();
    }

    @ThreadSafe
    public void changeCronExpression(final TestScenario testScenario, final String newCronExpression) {
        if (testScenario.isEnabled()) {
            scheduleTestScenarioService.unschedule(testScenario);
        }

        testScenario.setCron(newCronExpression);

        if (testScenario.isEnabled()) {
            scheduleTestScenarioService.schedule(testScenario);
        }
    }

    @ThreadSafe
    public void delete(final TestScenario testScenario) {
        lock.lock();
        try {
            if (testScenario.isEnabled()) {
                scheduleTestScenarioService.unschedule(testScenario);
            }

            ALL_TESTS.remove(testScenario.getId());
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

    private Predicate<TestScenario> getPredicateByNameCriteria(final String byNameCriteria) {
        Predicate<TestScenario> filterByNamePredicate = testScenario -> true;
        if (StringUtils.isNotEmpty(byNameCriteria)) {
            try {
                filterByNamePredicate = testScenario -> Pattern.compile(byNameCriteria, Pattern.CASE_INSENSITIVE).matcher(testScenario.getName()).find();
            } catch (PatternSyntaxException notUsed) {
                // In case of a syntax exception, no filtering on regex will be applied.
                // In this case, the tool will do a filtering based on a simple string contains.
                filterByNamePredicate = testScenario -> testScenario.getName().contains(byNameCriteria);
            }
        }

        return filterByNamePredicate;
    }

}
