package org.myalerts.app.layouts;

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
import org.myalerts.app.views.LoginView;
import org.myalerts.app.views.LogoutView;
import org.myalerts.app.views.SettingsView;
import org.myalerts.app.views.TestScenarioView;

import java.util.HashMap;
import java.util.Map;

@CssImport(value = "styles/shared-styles.css")
@CssImport(value = "styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
public class BaseLayout extends AppLayout implements BeforeEnterObserver {

    private static final boolean IS_OPTIMIZED_FOR_MOBILE = true;

    private static final Map<Class<?>, Tab> CLASS_TAB_MAP = new HashMap<>();

    private final Tabs tabs = new Tabs();

    public BaseLayout() {
        init();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        tabs.setSelectedTab(CLASS_TAB_MAP.get(beforeEnterEvent.getNavigationTarget()));
    }

    private void init() {
        setPrimarySection(Section.NAVBAR);

        Image img = new Image("logo.png", "Vaadin Logo"); // TODO: create our own logo
        img.setHeight("44px");
        addToNavbar(IS_OPTIMIZED_FOR_MOBILE, new DrawerToggle(), img);

        addMenuTab("Test scenarios", VaadinIcon.LIST.create(), TestScenarioView.class);
        if (VaadinSecurity.check().isAuthenticated()) {
            if (VaadinSecurity.check().hasRole("ROLE_ADMIN")) {
                addMenuTab("Settings", VaadinIcon.EDIT.create(), SettingsView.class);
            }
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
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        IconButton button = new IconButton(new Icon(VaadinIcon.PALETE));
        button.addClickListener(event -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        layout.add(button);
        return layout;
    }
}
