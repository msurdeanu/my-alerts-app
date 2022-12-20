package org.myalerts.repository;

import org.myalerts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(final String username);

}
