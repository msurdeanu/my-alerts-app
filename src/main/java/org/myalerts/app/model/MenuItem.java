package org.myalerts.app.model;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;

import org.myalerts.app.converter.ClassOfComponentToStringConverter;
import org.myalerts.app.converter.VaadinIconToStringConverter;

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

    @Convert(converter = VaadinIconToStringConverter.class)
    private VaadinIcon icon;

    @Convert(converter = ClassOfComponentToStringConverter.class)
    private Class<? extends Component> target;

    private int option;

    private int sequence;

}
