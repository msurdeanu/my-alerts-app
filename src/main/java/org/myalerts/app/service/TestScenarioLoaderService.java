package org.myalerts.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import org.myalerts.app.repository.TestScenarioRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestScenarioLoaderService {

    private final TestScenarioRepository testScenarioRepository;

    private final TestScenarioService testScenarioService;

    @Order(10)
    @EventListener(ApplicationReadyEvent.class)
    public void loadAll() {
        testScenarioRepository.findAllWithLastRunTime().forEach(testScenarioService::createAndSchedule);
    }

}
