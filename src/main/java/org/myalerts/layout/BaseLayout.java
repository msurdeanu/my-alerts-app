package org.myalerts.layout;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;

import org.myalerts.repository.MenuItemRepository;
import org.myalerts.transformer.MenuItemsToRouterLinksTransformer;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@CssImport(value = "styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
public class BaseLayout extends AppLayout {

    private static final boolean IS_OPTIMIZED_FOR_MOBILE = true;

    public BaseLayout(final MenuItemRepository menuItemRepository) {
        setPrimarySection(Section.DRAWER);

        final var logo = new Image(getTranslation("app.logo.src"), getTranslation("app.logo.alt"));
        logo.setHeight("44px");
        addToNavbar(IS_OPTIMIZED_FOR_MOBILE, new DrawerToggle(), logo);

        addToDrawer(createDrawerContent(new MenuItemsToRouterLinksTransformer().transform(menuItemRepository.findByOrderByPosition())));
    }

    private Component createDrawerContent(final List<RouterLink> routerLinks) {
        final var appName = new H2(getTranslation("app.name"));
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        final var mainMenu = new H3(getTranslation("menu.main"));
        mainMenu.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");

        final var section = new com.vaadin.flow.component.html.Section(appName, mainMenu, createNavigation(routerLinks), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation(final List<RouterLink> routerLinks) {
        final var nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");

        routerLinks.forEach(nav::add);
        return nav;
    }

    private Footer createFooter() {
        final var layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");
        layout.add(new Span("v1.0"));
        return layout;
    }

}
