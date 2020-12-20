package org.myalerts.app.interfaces;

import de.codecamp.vaadin.security.spring.access.SecuredRoute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SecuredRoute("hasRole('ROLE_USER')")
public @interface RequiresUserRole {

}

