package org.myalerts.service.event;

import lombok.RequiredArgsConstructor;
import org.myalerts.repository.TestScenarioRepository;
import org.myalerts.service.TestScenarioService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestScenarioReadyEventService {

    private final TestScenarioRepository testScenarioRepository;

    private final TestScenarioService testScenarioService;

    @Order(10)
    @EventListener(ApplicationReadyEvent.class)
    public void loadAll() {
        testScenarioRepository.findAll().forEach(testScenarioService::createAndSchedule);
    }

}
