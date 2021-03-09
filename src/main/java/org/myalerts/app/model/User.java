package org.myalerts.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    private boolean enabled;

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private String email;

    @Getter
    private String role;

}
