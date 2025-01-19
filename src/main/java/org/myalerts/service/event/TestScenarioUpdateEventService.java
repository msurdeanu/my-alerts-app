package org.myalerts.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.domain.event.EventListener;
import org.myalerts.domain.event.TestScenarioUpdateEvent;
import org.myalerts.repository.TestScenarioRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class TestScenarioUpdateEventService implements EventListener<TestScenarioUpdateEvent> {

    private final TestScenarioRepository testScenarioRepository;

    @Override
    public void onEventReceived(TestScenarioUpdateEvent event) {
        testScenarioRepository.save(event.getTestScenario());
    }

    @Override
    public Class<TestScenarioUpdateEvent> getEventType() {
        return TestScenarioUpdateEvent.class;
    }

}
