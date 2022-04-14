package org.myalerts.provider;

import org.myalerts.domain.StatisticsGroup;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface StatisticsProvider {

    StatisticsGroup getStatisticsGroup();

}
