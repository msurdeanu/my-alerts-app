package org.myalerts.view;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.view.component.StatisticsTreeGrid;
import org.myalerts.view.component.BaseLayout;
import org.myalerts.view.component.ResponsiveLayout;
import org.myalerts.provider.StatisticsProvider;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Slf4j
@PermitAll
@Route(value = StatisticView.ROUTE, layout = BaseLayout.class)
@DependsOn("pluginManager")
public class StatisticView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "statistics";

    public StatisticView(List<StatisticsProvider> statisticsProviders) {
        add(createHeader(getTranslation("statistic.page.subtitle")), createContent(new StatisticsTreeGrid(statisticsProviders)), createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.statistics"));
    }

}
