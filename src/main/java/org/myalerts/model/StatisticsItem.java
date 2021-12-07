package org.myalerts.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StatisticsItem {

    @EqualsAndHashCode.Include
    private String name;

    private String icon;

    private Object value;

    private String description;

}
