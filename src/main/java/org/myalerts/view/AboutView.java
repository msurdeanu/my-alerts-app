package org.myalerts.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.myalerts.provider.SettingProvider;
import org.myalerts.view.component.BaseLayout;
import org.myalerts.view.component.ResponsiveLayout;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@PermitAll
@Route(value = AboutView.ROUTE, layout = BaseLayout.class)
public class AboutView extends ResponsiveLayout implements HasDynamicTitle {

    public static final String ROUTE = "about";

    public AboutView(SettingProvider settingProvider) {
        add(createHeader(getTranslation("about.page.subtitle")), createContent(createBody(settingProvider)), createFooter());
    }

    private Component createBody(SettingProvider settingProvider) {
        return new Markdown(settingProvider.getOrDefault("aboutPageContent", StringUtils.EMPTY));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("site.base.title", getTranslation("menu.main.about"));
    }

}
