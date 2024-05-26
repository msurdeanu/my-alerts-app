package org.myalerts.domain.filter;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.PluginWrapper;

import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public final class PluginWrapperFilter implements Filter<PluginWrapper> {

    private String byNameCriteria = StringUtils.EMPTY;

    @Override
    public Predicate<PluginWrapper> getFilterPredicate() {
        return isNotEmpty(byNameCriteria)
            ? item -> containsIgnoreCase(item.getPluginId(), byNameCriteria)
            : item -> true;
    }

    public PluginWrapperFilter setByNameCriteria(final String byNameCriteria) {
        this.byNameCriteria = byNameCriteria;

        return this;
    }

}
