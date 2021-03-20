package org.myalerts.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.myalerts.app.model.TestScenarioResult;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioResultRepository extends JpaRepository<TestScenarioResult, Integer> {

    @Modifying
    @Query(value = "DELETE FROM results WHERE created <= datetime('now', '-7 day')", nativeQuery = true)
    void deleteAllOlderThanOneWeek();

}