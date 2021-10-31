package org.myalerts.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.myalerts.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioRepository extends JpaRepository<TestScenario, Integer> {

    @Query(value = "select s.*, t.last_run_time, coalesce(t.failed, false) as failed from scenarios s left join "
        + "(select scenario_id, cause is not null as failed, max(created) as last_run_time from results group by scenario_id) t on s.id = t.scenario_id",
        nativeQuery = true)
    List<TestScenario> findAll();

    @Transactional
    @Modifying
    @Query(value = "update scenarios set enabled = :enabled, name = :name, cron = :cron, definition = :definition where id = :id", nativeQuery = true)
    void update(@Param("id") Integer id, @Param("enabled") boolean enabled, @Param("name") String name, @Param("cron") String cron,
                @Param("definition") String definition);

    @Transactional
    @Modifying
    @Query(value = "delete from scenarios where id = :id", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

}
