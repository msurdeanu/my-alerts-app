package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.event.EventListener;
import org.myalerts.domain.event.TestDeleteEvent;
import org.myalerts.repository.TestScenarioRepository;
import org.springframework.stereotype.Service;

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
        testScenarioRepository.delete(event.getTestScenario());
    }

    @Override
    public Class<TestDeleteEvent> getEventType() {
        return TestDeleteEvent.class;
    }

}
