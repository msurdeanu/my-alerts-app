package org.myalerts.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.app.model.MenuItem;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findByOrderBySequenceAsc();

}
