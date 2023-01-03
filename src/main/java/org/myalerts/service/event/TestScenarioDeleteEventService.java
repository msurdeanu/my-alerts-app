package org.myalerts.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.event.EventListener;
import org.myalerts.domain.event.TestScenarioDeleteEvent;
import org.myalerts.repository.TestScenarioRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestScenarioDeleteEventService implements EventListener<TestScenarioDeleteEvent> {

    private final TestScenarioRepository testScenarioRepository;

    @Override
    public void onEventReceived(final TestScenarioDeleteEvent event) {
        testScenarioRepository.delete(event.getTestScenario());
    }

    @Override
    public Class<TestScenarioDeleteEvent> getEventType() {
        return TestScenarioDeleteEvent.class;
    }

}
