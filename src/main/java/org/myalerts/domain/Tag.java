package org.myalerts.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Entity
@Table(name = "tags")
@NoArgsConstructor
public class Tag implements Comparable<Tag> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @NaturalId
    private String name;

    public Tag(final String name) {
        this.name = name;
    }

    @Override
    public int compareTo(final Tag other) {
        return name.compareTo(other.name);
    }

}
