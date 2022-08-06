package org.myalerts.view;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.component.StatisticsTreeGrid;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.provider.StatisticsProvider;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@AnonymousAllowed
@Route(value = StatisticView.ROUTE, layout = BaseLayout.class)
@DependsOn("pluginManager")
public class StatisticView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "statistics";

    public StatisticView(final List<StatisticsProvider> statisticsProviders) {
        add(createHeader(getTranslation("statistic.page.subtitle")), createContent(new StatisticsTreeGrid(statisticsProviders)), createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.statistics"));
    }

}
