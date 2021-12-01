package org.myalerts.converter;

import org.junit.jupiter.api.Test;
import org.myalerts.view.AboutView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class ClassOfComponentToStringConverterTest {

    @Test
    public void testConvertToDatabaseColumn() {
        final var classOfComponentToStringConverter = new ClassOfComponentToStringConverter();

        assertNull(classOfComponentToStringConverter.convertToDatabaseColumn(null));
        assertEquals("org.myalerts.view.AboutView", classOfComponentToStringConverter.convertToDatabaseColumn(AboutView.class));
    }

    @Test
    public void testConvertToEntityAttribute() {
        final var classOfComponentToStringConverter = new ClassOfComponentToStringConverter();

        assertNull(classOfComponentToStringConverter.convertToEntityAttribute(null));
        assertNull(classOfComponentToStringConverter.convertToEntityAttribute("org.myalerts.view.NotFoundView"));
        assertEquals(AboutView.class, classOfComponentToStringConverter.convertToEntityAttribute("org.myalerts.view.AboutView"));
    }

}
