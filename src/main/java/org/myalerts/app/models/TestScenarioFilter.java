package org.myalerts.app.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class TestScenarioFilter {

    private TestScenarioType byTypeCriteria = TestScenarioType.ALL;

    private String byNameCriteria = StringUtils.EMPTY;

}
