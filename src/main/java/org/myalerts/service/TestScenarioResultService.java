package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.api.event.EventListener;
import org.myalerts.domain.TestScenarioResult;
import org.myalerts.domain.event.TestResultEvent;
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
public class TestScenarioResultService implements EventListener<TestResultEvent> {

    private final TestScenarioResultRepository testScenarioResultRepository;

    @Cacheable(cacheNames = "test-scenario-results", cacheManager = "testScenarioResultCacheManager", key = "#id")
    public Collection<TestScenarioResult> getLastResults(final int id) {
        return testScenarioResultRepository.findByScenarioIdOrderByCreatedDesc(id, PageRequest.of(0, 10));
    }

    @CacheEvict(cacheNames = "test-scenario-results", cacheManager = "testScenarioResultCacheManager", key = "#event.testScenario?.id")
    @Override
    public void onEventReceived(final TestResultEvent event) {
        testScenarioResultRepository.save(event.getTestScenarioResult());
    }

    @Override
    public Class<TestResultEvent> getEventType() {
        return TestResultEvent.class;
    }

}
