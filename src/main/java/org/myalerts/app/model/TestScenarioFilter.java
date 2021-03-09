package org.myalerts.app.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
@Setter
public class TestScenarioFilter {

    private TestScenarioType byTypeCriteria = TestScenarioType.ALL;

    private String byNameCriteria = StringUtils.EMPTY;

}
