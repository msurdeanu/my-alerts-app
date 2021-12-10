package org.myalerts.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.model.MenuItem;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    @Cacheable(cacheNames = "menu-items", cacheManager = "menuItemCacheManager")
    List<MenuItem> findByOrderByPosition();

}
