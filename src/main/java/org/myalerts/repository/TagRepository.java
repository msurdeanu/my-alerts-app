package org.myalerts.repository;

import org.myalerts.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByName(final String name);

    default Tag getOrCreate(final String name) {
        return findByName(name).orElseGet(() -> new Tag(name));
    }

}
