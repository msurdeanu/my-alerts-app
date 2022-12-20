package org.myalerts.domain;

import lombok.Getter;
import lombok.Setter;
import org.myalerts.converter.SettingTypeToStringConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Getter
@Table(name = "settings")
public class Setting {

    @Id
    private String key;

    private String title;

    private String description;

    @Convert(converter = SettingTypeToStringConverter.class)
    private SettingType type;

    @Setter
    private String value;

    private boolean editable;

    private int position;

    @Transient
    @Setter
    private Object computedValue;

}
