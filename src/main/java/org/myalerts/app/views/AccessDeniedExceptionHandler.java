package org.myalerts.app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import de.codecamp.vaadin.security.spring.access.RouteAccessDeniedException;

import javax.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class AccessDeniedExceptionHandler extends Component implements HasErrorParameter<RouteAccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<RouteAccessDeniedException> parameter)
    {
        getElement().setText("AccessDeniedExceptionHandler: Tried to navigate to a view without "
                + "correct access rights. You can show an error page, show an error dialog/notification"
                + " and/or forward to a different view.");
        return HttpServletResponse.SC_FORBIDDEN;
    }

}

