package org.myalerts.domain;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vaadin.flow.component.Component;
import lombok.Getter;

import org.myalerts.converter.ClassOfComponentToStringConverter;
import org.myalerts.converter.UserRoleToStringConverter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Getter
@Table(name = "menu_items")
public class MenuItem {

    @Id
    private String label;

    private String icon;

    @Convert(converter = ClassOfComponentToStringConverter.class)
    private Class<? extends Component> target;

    @Convert(converter = UserRoleToStringConverter.class)
    private UserRole role;

    private int position;

}
