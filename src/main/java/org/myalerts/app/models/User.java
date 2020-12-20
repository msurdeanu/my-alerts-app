package org.myalerts.app.models;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
