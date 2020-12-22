package org.myalerts.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.RequiredArgsConstructor;
import org.myalerts.app.providers.CustomI18NProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@RequiredArgsConstructor
public class Application extends SpringBootServletInitializer implements AppShellConfigurator, VaadinServiceInitListener {

    //private final CookieStoreService cookieStoreService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        System.setProperty("vaadin.i18n.provider", CustomI18NProvider.class.getName());

        /*
        TODO
        if (cookieStoreService.isSetAndTrue(CookieStoreService.THEME_DARK_COOKIE)) {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.LIGHT)) {
                themeList.add(Lumo.DARK);
            }
        }
         */
    }

}
