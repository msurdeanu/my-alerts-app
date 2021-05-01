package org.myalerts.app.layout;

import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.Lumo;

import org.myalerts.app.component.IconButton;
import org.myalerts.app.repository.MenuItemRepository;
import org.myalerts.app.service.CookieStoreService;
import org.myalerts.app.transformer.MenuItemsToTabMapTransformer;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@CssImport(value = "styles/shared-styles.css")
@CssImport(value = "styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
public class BaseLayout extends AppLayout implements BeforeEnterObserver {

    private static final boolean IS_OPTIMIZED_FOR_MOBILE = true;

    private final Map<Class<?>, Tab> tabMap;

    private final Tabs tabs = new Tabs();

    private final CookieStoreService cookieStoreService;

    public BaseLayout(CookieStoreService cookieStoreService, MenuItemRepository menuItemRepository) {
        this.cookieStoreService = cookieStoreService;
        this.tabMap = new MenuItemsToTabMapTransformer().transform(menuItemRepository.findByOrderBySequenceAsc());

        init();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(tabMap.get(beforeEnterEvent.getNavigationTarget()));
    }

    private void init() {
        setPrimarySection(Section.NAVBAR);

        final Image logo = new Image("logo.png", "Logo");
        logo.setHeight("44px");
        addToNavbar(IS_OPTIMIZED_FOR_MOBILE, new DrawerToggle(), logo);

        tabMap.values().stream().forEach(tabs::add);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(new Hr(), tabs, new Hr(), createMenuButtons());

        initTheme();
    }

    private Component createMenuButtons() {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        final IconButton themeButton = new IconButton(new Icon(VaadinIcon.PALETE));
        themeButton.addClickListener(event -> toggleDarkTheme());

        verticalLayout.add(themeButton);
        return verticalLayout;
    }

    private void initTheme() {
        final ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (cookieStoreService.isSetAndTrue(CookieStoreService.THEME_DARK_COOKIE) && !themeList.contains(Lumo.DARK)) {
            themeList.add(Lumo.DARK);
        }
    }

    private void toggleDarkTheme() {
        final ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
            cookieStoreService.set(CookieStoreService.THEME_DARK_COOKIE, false);
        } else {
            themeList.add(Lumo.DARK);
            cookieStoreService.set(CookieStoreService.THEME_DARK_COOKIE, true);
        }
    }

}
