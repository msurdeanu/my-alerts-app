package org.myalerts.app.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
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

    @Cacheable(cacheNames = "testScenarioResultsPerScenarioId", cacheManager = "testScenarioResultCacheManager")
    List<TestScenarioResult> findByScenarioIdOrderByCreatedDesc(final int scenarioId, final Pageable pageable);

}