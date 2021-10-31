package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.myalerts.event.EventListener;
import org.myalerts.event.TestDeleteEvent;
import org.myalerts.repository.TestScenarioRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestScenarioDeleteService implements EventListener<TestDeleteEvent> {

    private final TestScenarioRepository testScenarioRepository;

    @Override
    public void onEventReceived(final TestDeleteEvent event) {
        testScenarioRepository.deleteById(event.getTestScenario().getId());
    }

    @Override
    public Class<TestDeleteEvent> getEventType() {
        return TestDeleteEvent.class;
    }

}
