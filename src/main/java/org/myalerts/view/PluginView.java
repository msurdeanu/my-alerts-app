package org.myalerts.view;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.myalerts.component.PluginGrid;
import org.myalerts.domain.PluginFilter;
import org.myalerts.layout.BaseLayout;
import org.myalerts.layout.ResponsiveLayout;
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

    private final PluginFilter pluginFilter = new PluginFilter();

    private final PluginGrid pluginGrid;

    public PluginView(final PluginService pluginService) {
        super();

        final ConfigurableFilterDataProvider<PluginWrapper, Void, PluginFilter> configurableFilterDataProvider = DataProvider
            .fromFilteringCallbacks(pluginService::findBy, pluginService::countBy)
            .withConfigurableFilter();
        configurableFilterDataProvider.setFilter(pluginFilter);

        pluginGrid = new PluginGrid();
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
