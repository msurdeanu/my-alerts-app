package org.myalerts.domain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
public final class PluginFilter {

    private String byNameCriteria = StringUtils.EMPTY;

    public PluginFilter setByNameCriteria(final String byNameCriteria) {
        this.byNameCriteria = byNameCriteria;

        return this;
    }

}
