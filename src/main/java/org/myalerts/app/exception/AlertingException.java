package org.myalerts.app.exception;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public class AlertingException extends RuntimeException {

    public AlertingException(final String message) {
        super(message);
    }

    public AlertingException(final Throwable throwable) {
        super(throwable);
    }

    public AlertingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
