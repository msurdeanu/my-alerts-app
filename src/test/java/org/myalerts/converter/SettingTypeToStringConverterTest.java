package org.myalerts.converter;

import org.junit.jupiter.api.Test;

import org.myalerts.model.SettingType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public final class SettingTypeToStringConverterTest {

    @Test
    public void testConvertToDatabaseColumn() {
        final var settingTypeToStringConverter = new SettingTypeToStringConverter();

        assertNull(settingTypeToStringConverter.convertToDatabaseColumn(null));
        assertEquals("text", settingTypeToStringConverter.convertToDatabaseColumn(SettingType.TEXT));
        assertEquals("pass", settingTypeToStringConverter.convertToDatabaseColumn(SettingType.PASSWORD));
        assertEquals("int", settingTypeToStringConverter.convertToDatabaseColumn(SettingType.INTEGER));
        assertEquals("bool", settingTypeToStringConverter.convertToDatabaseColumn(SettingType.BOOLEAN));
    }

    @Test
    public void testConvertToEntityAttribute() {
        final var settingTypeToStringConverter = new SettingTypeToStringConverter();

        assertNull(settingTypeToStringConverter.convertToEntityAttribute(null));
        assertEquals(SettingType.TEXT, settingTypeToStringConverter.convertToEntityAttribute("text"));
        assertEquals(SettingType.PASSWORD, settingTypeToStringConverter.convertToEntityAttribute("pass"));
        assertEquals(SettingType.INTEGER, settingTypeToStringConverter.convertToEntityAttribute("int"));
        assertEquals(SettingType.BOOLEAN, settingTypeToStringConverter.convertToEntityAttribute("bool"));
        assertNull(settingTypeToStringConverter.convertToEntityAttribute("none"));
    }

}
