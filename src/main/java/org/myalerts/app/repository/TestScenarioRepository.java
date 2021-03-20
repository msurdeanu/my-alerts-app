package org.myalerts.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.app.model.TestScenario;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioRepository extends JpaRepository<TestScenario, Integer> {

}
