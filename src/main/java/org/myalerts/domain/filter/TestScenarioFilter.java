package org.myalerts.domain.filter;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.domain.TestScenario;
import org.myalerts.domain.TestScenarioType;

import java.util.Set;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public final class TestScenarioFilter implements Filter<TestScenario> {

    private TestScenarioType byTypeCriteria = TestScenarioType.ALL;

    private String byNameCriteria = StringUtils.EMPTY;

    private Set<String> byTagCriteria = Set.of();

    @Override
    public Predicate<TestScenario> getFilterPredicate() {
        final Predicate<TestScenario> predicate = byTypeCriteria.getFilter();
        return predicate.and(!byTagCriteria.isEmpty()
                ? item -> item.getTagsAsString().containsAll(byTagCriteria)
                : item -> true)
            .and(isNotEmpty(byNameCriteria)
                ? item -> containsIgnoreCase(item.getName(), byNameCriteria)
                : item -> true);
    }

    public TestScenarioFilter setByTypeCriteria(final TestScenarioType byTypeCriteria) {
        this.byTypeCriteria = byTypeCriteria;

        return this;
    }

    public TestScenarioFilter setByNameCriteria(final String byNameCriteria) {
        this.byNameCriteria = byNameCriteria;

        return this;
    }

    public TestScenarioFilter setByTagCriteria(final Set<String> byTagCriteria) {
        this.byTagCriteria = byTagCriteria;

        return this;
    }

}
