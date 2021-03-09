package org.myalerts.app.repository;

import java.util.List;

import org.myalerts.app.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findAllByOrderBySequence();

}
