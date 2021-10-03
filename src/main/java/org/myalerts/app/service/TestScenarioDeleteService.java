package org.myalerts.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.myalerts.app.event.EventListener;
import org.myalerts.app.event.TestDeleteEvent;
import org.myalerts.app.repository.TestScenarioRepository;

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
        testScenarioRepository.save(event.getTestScenario());
    }

    @Override
    public Class<TestDeleteEvent> getEventType() {
        return TestDeleteEvent.class;
    }

}
