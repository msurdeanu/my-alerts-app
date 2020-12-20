package org.myalerts.app.services;

import com.vaadin.flow.data.provider.Query;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.app.interfaces.markers.ThreadSafe;
import org.myalerts.app.models.TestScenario;
import org.myalerts.app.models.TestScenarioFilter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestScenarioService {

    private static final Map<String, TestScenario> allTests = new HashMap<>();

    private final ScheduleTestScenarioService scheduleTestScenarioService;

    static {
        // TODO: Remove dummy test creation
        for (int i = 0; i < 10; i++) {
            TestScenario testScenario = new TestScenario();
            testScenario.setName("Test scenario " + i);
            testScenario.setCronExpression("0 */10 * * * MON-FRI");

            allTests.put(testScenario.getId(), testScenario);
        }
        for (int i = 10; i < 15; i++) {
            TestScenario testScenario = new TestScenario();
            testScenario.setName("Test scenario " + i);
            testScenario.setCronExpression("0 */5 * * * MON-FRI");
            testScenario.setFailed(true);

            allTests.put(testScenario.getId(), testScenario);
        }
        for (int i = 15; i < 20; i++) {
            TestScenario testScenario = new TestScenario();
            testScenario.setName("Test scenario " + i);
            testScenario.setCronExpression("0 */5 * * * MON-FRI");
            testScenario.setEnabled(false);

            allTests.put(testScenario.getId(), testScenario);
        }
    }

    public TestScenarioService(ScheduleTestScenarioService scheduleTestScenarioService) {
        this.scheduleTestScenarioService = scheduleTestScenarioService;
    }

    public List<TestScenario> getAll() {
        return getAll(0, Long.MAX_VALUE, StringUtils.EMPTY);
    }

    public List<TestScenario> getAll(String byNameCriteria) {
        return getAll(0, Long.MAX_VALUE, byNameCriteria);
    }

    public List<TestScenario> getAll(long offset, long limit, String byNameCriteria) {
        return allTests.values().stream()
                .filter(getPredicateByNameCriteria(byNameCriteria))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getAllSize() {
        return getAll().size();
    }

    public int getAllSize(String byNameCriteria) {
        return getAll(byNameCriteria).size();
    }

    public int getAllSize(long offset, long limit, String byNameCriteria) {
        return getAll(offset, limit, byNameCriteria).size();
    }

    public List<TestScenario> getAllFailed(long offset, long limit, String byNameCriteria) {
        return allTests.values().stream()
                .filter(TestScenario::isFailed)
                .filter(getPredicateByNameCriteria(byNameCriteria))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getAllFailedSize(long offset, long limit, String filterByName) {
        return getAllFailed(offset, limit, filterByName).size();
    }

    public List<TestScenario> getAllPassed(long offset, long limit, String byNameCriteria) {
        return allTests.values().stream()
                .filter(testScenario -> !testScenario.isFailed())
                .filter(getPredicateByNameCriteria(byNameCriteria))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getAllPassedSize(long offset, long limit, String filterByName) {
        return getAllPassed(offset, limit, filterByName).size();
    }

    public List<TestScenario> getAllDisabled(long offset, long limit, String byNameCriteria) {
        return allTests.values().stream()
                .filter(testScenario -> !testScenario.isEnabled())
                .filter(getPredicateByNameCriteria(byNameCriteria))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getAllDisabledSize(long offset, long limit, String filterByName) {
        return getAllDisabled(offset, limit, filterByName).size();
    }

    public Stream<TestScenario> findBy(Query<TestScenario, TestScenarioFilter> query) {
        Optional<TestScenarioFilter> testScenarioFilterOptional = query.getFilter();
        if (testScenarioFilterOptional.isEmpty()) {
            return getAll().stream();
        }

        TestScenarioFilter testScenarioFilter = testScenarioFilterOptional.get();
        switch (testScenarioFilter.getByTypeCriteria()) {
            case FAILED:
                return getAllFailed(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria()).stream();
            case PASSED:
                return getAllPassed(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria()).stream();
            case DISABLED:
                return getAllDisabled(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria()).stream();
            default:
                return getAll(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria()).stream();
        }
    }

    public int countBy(Query<TestScenario, TestScenarioFilter> query) {
        Optional<TestScenarioFilter> testScenarioFilterOptional = query.getFilter();
        if (testScenarioFilterOptional.isEmpty()) {
            return getAllSize();
        }

        TestScenarioFilter testScenarioFilter = testScenarioFilterOptional.get();
        switch (testScenarioFilter.getByTypeCriteria()) {
            case FAILED:
                return getAllFailedSize(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria());
            case PASSED:
                return getAllPassedSize(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria());
            case DISABLED:
                return getAllDisabledSize(query.getOffset(), query.getLimit(), testScenarioFilter.getByNameCriteria());
            default:
                return getAllSize(testScenarioFilter.getByNameCriteria());
        }
    }

    @ThreadSafe
    public void changeActivation(TestScenario testScenario) {
        if (testScenario.isEnabled()) {
            scheduleTestScenarioService.remove(testScenario);
        } else {
            scheduleTestScenarioService.add(testScenario);
        }

        testScenario.toggleOnEnabling();
    }

    @ThreadSafe
    public void changeCronExpression(TestScenario testScenario, String newCronExpression) {
        if (testScenario.isEnabled()) {
            scheduleTestScenarioService.remove(testScenario);
        }

        testScenario.setCronExpression(newCronExpression);

        if (testScenario.isEnabled()) {
            scheduleTestScenarioService.add(testScenario);
        }
    }

    public void scheduleAllNowInAsyncMode() {
        allTests.values().stream()
                .filter(TestScenario::isEnabled)
                .forEach(scheduleTestScenarioService::scheduleInAsyncMode);
    }

    public void scheduleNowInSyncMode(TestScenario testScenario) throws InterruptedException, ExecutionException, TimeoutException {
        scheduleTestScenarioService.scheduleInSyncMode(testScenario);
    }

    private Predicate<TestScenario> getPredicateByNameCriteria(String byNameCriteria) {
        Predicate<TestScenario> filterByNamePredicate = testScenario -> true;
        if (StringUtils.isNotEmpty(byNameCriteria)) {
            try {
                final Pattern filterByNamePattern = Pattern.compile(byNameCriteria, Pattern.CASE_INSENSITIVE);
                filterByNamePredicate = testScenario -> filterByNamePattern.matcher(testScenario.getName()).find();
            } catch (PatternSyntaxException e) {
                // In case of a syntax exception, no filtering on regex will be applied.
                // In this case, the tool will do a filtering based on a simple string contains.
                filterByNamePredicate = testScenario -> testScenario.getName().contains(byNameCriteria);
            }
        }

        return filterByNamePredicate;
    }

}
