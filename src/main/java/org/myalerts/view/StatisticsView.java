package org.myalerts.view;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.myalerts.component.StatisticsTreeGrid;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
import org.myalerts.provider.StatisticsProvider;

import java.util.List;

@Slf4j
@AnonymousAllowed
@Route(value = StatisticsView.ROUTE, layout = BaseLayout.class)
public class StatisticsView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "statistics";

    public StatisticsView(final List<StatisticsProvider> statisticsProviders) {
        add(createHeader(getTranslation("statistics.page.subtitle")), createContent(new StatisticsTreeGrid(statisticsProviders)), createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.statistics"));
    }

}
