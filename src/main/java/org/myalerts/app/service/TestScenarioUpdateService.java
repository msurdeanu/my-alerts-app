package org.myalerts.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.myalerts.app.event.EventListener;
import org.myalerts.app.event.TestUpdateEvent;
import org.myalerts.app.repository.TestScenarioRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestScenarioUpdateService implements EventListener<TestUpdateEvent> {

    private final TestScenarioRepository testScenarioRepository;

    @Override
    public void onEventReceived(final TestUpdateEvent event) {
        testScenarioRepository.save(event.getTestScenario());
    }

    @Override
    public Class<TestUpdateEvent> getEventType() {
        return TestUpdateEvent.class;
    }

}
