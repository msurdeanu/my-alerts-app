package org.myalerts.app.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.codecamp.vaadin.security.spring.access.SecuredAccess;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SecuredAccess("hasRole('ROLE_ADMIN')")
public @interface RequiresAdminRole {

}

