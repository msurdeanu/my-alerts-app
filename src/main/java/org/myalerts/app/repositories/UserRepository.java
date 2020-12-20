package org.myalerts.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.myalerts.app.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByUsername(String username);

}
