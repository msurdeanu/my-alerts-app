package org.myalerts.app.repository;

import org.myalerts.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByUsername(String username);

}
