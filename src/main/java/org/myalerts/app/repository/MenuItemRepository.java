package org.myalerts.app.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.app.model.MenuItem;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@CacheConfig(cacheNames = "menuItemsCache")
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    @Cacheable
    List<MenuItem> findByOrderByPosition();

}
