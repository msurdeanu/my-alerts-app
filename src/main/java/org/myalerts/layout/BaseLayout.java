package org.myalerts.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.myalerts.repository.MenuItemRepository;
import org.myalerts.transformer.MenuItemsToSideNavItemsTransformer;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class BaseLayout extends AppLayout {

    private static final boolean IS_OPTIMIZED_FOR_MOBILE = true;

    public BaseLayout(final MenuItemRepository menuItemRepository) {
        setPrimarySection(Section.DRAWER);

        final var logo = new Image(getTranslation("app.logo.src"), getTranslation("app.logo.alt"));
        logo.setHeight("44px");
        addToNavbar(IS_OPTIMIZED_FOR_MOBILE, new DrawerToggle(), logo);

        addDrawerContent(new MenuItemsToSideNavItemsTransformer().transform(menuItemRepository.findByOrderByPosition()));
    }

    private void addDrawerContent(final List<SideNavItem> routerLinks) {
        final var appName = new H1(getTranslation("app.name"));
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        final var header = new Header(appName);
        final var scroller = new Scroller(createNavigation(routerLinks));
        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation(final List<SideNavItem> routerLinks) {
        final var appNav = new SideNav();
        routerLinks.forEach(appNav::addItem);
        return appNav;
    }

    private Footer createFooter() {
        final var layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");
        layout.add(new Span("v1.0")); // TODO: Use version from settings table
        return layout;
    }

}
