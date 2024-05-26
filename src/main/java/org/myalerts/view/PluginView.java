package org.myalerts.view;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.myalerts.view.component.PluginGrid;
import org.myalerts.domain.filter.PluginWrapperFilter;
import org.myalerts.view.component.BaseLayout;
import org.myalerts.view.component.ResponsiveLayout;
import org.myalerts.service.PluginService;
import org.pf4j.PluginWrapper;

import jakarta.annotation.security.RolesAllowed;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RolesAllowed("ROLE_ADMIN")
@Route(value = PluginView.ROUTE, layout = BaseLayout.class)
public class PluginView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "plugins";

    public PluginView(final PluginService pluginService) {
        super();

        final ConfigurableFilterDataProvider<PluginWrapper, Void, PluginWrapperFilter> configurableFilterDataProvider = DataProvider
            .fromFilteringCallbacks(pluginService::findBy, pluginService::countBy)
            .withConfigurableFilter();
        configurableFilterDataProvider.setFilter(new PluginWrapperFilter());

        final var pluginGrid = new PluginGrid();
        pluginGrid.setDataProvider(configurableFilterDataProvider);

        add(createHeader(getTranslation("plugin.page.subtitle")));
        add(createContent(pluginGrid));
        add(createFooter());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.plugins"));
    }

}
