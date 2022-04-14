package org.myalerts.repository;

import org.myalerts.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findAllByOrderByPosition();

}
