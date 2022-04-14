package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.repository.TestScenarioResultRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MidnightCleanupService {

    private final TestScenarioResultRepository testScenarioResultRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void doCleanup() {
        try {
            testScenarioResultRepository.deleteAllOlderThanOneWeek();
        } catch (Exception e) {
            log.error("An error occurred during midnight cleanup process.", e);
        }
    }

}
