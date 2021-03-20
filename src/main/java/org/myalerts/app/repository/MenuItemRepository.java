package org.myalerts.app.repository;

import java.util.List;

import org.myalerts.app.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findByOrderBySequenceAsc();

}
