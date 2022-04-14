package org.myalerts.repository;

import org.myalerts.domain.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioRepository extends JpaRepository<TestScenario, Integer> {

    @Query(value = "select * from scenario_results", nativeQuery = true)
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
