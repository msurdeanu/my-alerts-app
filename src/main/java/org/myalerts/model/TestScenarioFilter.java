package org.myalerts.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public class TestScenarioFilter {

    private TestScenarioType byTypeCriteria = TestScenarioType.ALL;

    private String byNameCriteria = StringUtils.EMPTY;

    public TestScenarioFilter setByTypeCriteria(final TestScenarioType byTypeCriteria) {
        this.byTypeCriteria = byTypeCriteria;

        return this;
    }

    public TestScenarioFilter setByNameCriteria(final String byNameCriteria) {
        this.byNameCriteria = byNameCriteria;

        return this;
    }

}
