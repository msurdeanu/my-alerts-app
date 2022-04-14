package org.myalerts.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Builder
@Getter
public class StatisticsGroup {

    private StatisticsItem root;

    private List<StatisticsItem> leafs;

}
