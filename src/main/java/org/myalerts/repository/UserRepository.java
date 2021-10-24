package org.myalerts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.model.User;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByUsername(String username);

}
