package org.myalerts.app.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import org.myalerts.app.event.EventListener;
import org.myalerts.app.event.TestResultEvent;
import org.myalerts.app.model.TestScenario;
import org.myalerts.app.repository.TestScenarioRepository;
import org.myalerts.app.repository.TestScenarioResultRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@Order(5)
@RequiredArgsConstructor
public class TestScenarioResultService implements EventListener<TestResultEvent> {

    private final TestScenarioRepository testScenarioRepository;

    private final TestScenarioResultRepository testScenarioResultRepository;

    @Override
    public void onEventReceived(TestResultEvent event) {
        testScenarioResultRepository.save(event.getTestScenarioResult());

        Optional.ofNullable(event.getTestScenario())
            .filter(TestScenario::isDirtyFlag)
            .ifPresent(testScenario -> {
                testScenarioRepository.save(testScenario);
                testScenario.resetDirtyFlag();

                log.info("Test scenario {} was updated in database since dirty flag was set.", testScenario.getId());
            });
    }

    @Override
    public Class<TestResultEvent> getEventType() {
        return TestResultEvent.class;
    }

}
