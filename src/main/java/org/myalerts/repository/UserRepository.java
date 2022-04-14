package org.myalerts.repository;

import org.myalerts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByUsername(String username);

}
