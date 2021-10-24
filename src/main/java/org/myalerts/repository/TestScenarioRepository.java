package org.myalerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.myalerts.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioRepository extends JpaRepository<TestScenario, Integer> {

    @Query(value = "select s.*, t.last_run_time from scenarios s "
        + "left join (select scenario_id, max(created) as last_run_time from results group by scenario_id) t on s.id = t.scenario_id", nativeQuery = true)
    List<TestScenario> findAllWithLastRunTime();

}
