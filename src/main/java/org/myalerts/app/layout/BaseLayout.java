package org.myalerts.app.layout;

import java.util.HashMap;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import de.codecamp.vaadin.security.spring.access.VaadinSecurity;
import org.myalerts.app.components.IconButton;
import org.myalerts.app.service.CookieStoreService;
import org.myalerts.app.view.LoginView;
import org.myalerts.app.view.LogoutView;
import org.myalerts.app.view.SettingsView;
import org.myalerts.app.view.TestScenarioView;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@CssImport(value = "styles/shared-styles.css")
@CssImport(value = "styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
public class BaseLayout extends AppLayout implements BeforeEnterObserver {

    private static final boolean IS_OPTIMIZED_FOR_MOBILE = true;

    private static final Map<Class<?>, Tab> CLASS_TAB_MAP = new HashMap<>();

    private final Tabs tabs = new Tabs();

    private final CookieStoreService cookieStoreService;

    public BaseLayout(CookieStoreService cookieStoreService) {
        this.cookieStoreService = cookieStoreService;

        init();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(CLASS_TAB_MAP.get(beforeEnterEvent.getNavigationTarget()));
    }

    private void init() {
        setPrimarySection(Section.NAVBAR);

        Image img = new Image("logo.png", "Logo");
        img.setHeight("44px");
        addToNavbar(IS_OPTIMIZED_FOR_MOBILE, new DrawerToggle(), img);

        addMenuTab("Test scenarios", VaadinIcon.LIST.create(), TestScenarioView.class);
        addMenuTab("Settings", VaadinIcon.EDIT.create(), SettingsView.class);
        if (VaadinSecurity.check().isAuthenticated()) {
            addMenuTab("Logout", VaadinIcon.SIGN_OUT.create(), LogoutView.class);
        } else {
            addMenuTab("Login", VaadinIcon.SIGN_IN.create(), LoginView.class);
        }

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(new Hr(), tabs, new Hr(), createMenuButtons());
    }

    private void addMenuTab(String label, Icon icon, Class<? extends Component> target) {
        HorizontalLayout layout = new HorizontalLayout(icon, new RouterLink(label, target));

        Tab tab = new Tab(layout);
        CLASS_TAB_MAP.put(target, tab);
        tabs.add(tab);
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
