package org.myalerts.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.TestScenarioResult;
import org.myalerts.domain.event.EventListener;
import org.myalerts.domain.event.TestScenarioRunEvent;
import org.myalerts.repository.TestScenarioResultRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestScenarioRunEventService implements EventListener<TestScenarioRunEvent> {

    private final TestScenarioResultRepository testScenarioResultRepository;

    @Cacheable(cacheNames = "test-scenario-results", cacheManager = "testScenarioResultCacheManager", key = "#id")
    public Collection<TestScenarioResult> getLastResults(final int id) {
        return testScenarioResultRepository.findByScenarioIdOrderByCreatedDesc(id, PageRequest.of(0, 10));
    }

    @CacheEvict(cacheNames = "test-scenario-results", cacheManager = "testScenarioResultCacheManager", key = "#event.testScenarioRun?.scenarioId")
    @Override
    public void onEventReceived(final TestScenarioRunEvent event) {
        testScenarioResultRepository.save(TestScenarioResult.from(event.getTestScenarioRun()));


    }

    @Override
    public Class<TestScenarioRunEvent> getEventType() {
        return TestScenarioRunEvent.class;
    }

}
