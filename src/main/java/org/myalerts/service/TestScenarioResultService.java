package org.myalerts.service;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.myalerts.event.EventListener;
import org.myalerts.event.TestResultEvent;
import org.myalerts.model.TestScenarioResult;
import org.myalerts.repository.TestScenarioResultRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestScenarioResultService implements EventListener<TestResultEvent> {

    private final TestScenarioResultRepository testScenarioResultRepository;

    public Collection<TestScenarioResult> getLastResults(final int testScenarioId) {
        return testScenarioResultRepository.findByScenarioIdOrderByCreatedDesc(testScenarioId, PageRequest.of(0, 10));
    }

    @Override
    public void onEventReceived(final TestResultEvent event) {
        testScenarioResultRepository.save(event.getTestScenarioResult());
    }

    @Override
    public Class<TestResultEvent> getEventType() {
        return TestResultEvent.class;
    }

}
