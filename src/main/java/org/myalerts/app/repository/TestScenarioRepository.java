package org.myalerts.app.repository;

import org.myalerts.app.model.TestScenario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TestScenarioRepository extends JpaRepository<TestScenario, Integer> {

}
