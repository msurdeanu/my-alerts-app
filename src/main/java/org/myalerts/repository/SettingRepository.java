package org.myalerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.myalerts.model.Setting;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findAllByOrderByPosition();

}
