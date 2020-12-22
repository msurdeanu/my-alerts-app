package org.myalerts.app.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplaySetting {
    Type type();
    String label();
    String helper();

    enum Type {
        TEXT, PASSWORD, INTEGER;
    }
}
