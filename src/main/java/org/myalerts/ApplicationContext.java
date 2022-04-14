package org.myalerts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.myalerts.provider.SettingProvider;
import org.springframework.stereotype.Component;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Getter
@Component
@RequiredArgsConstructor
public class ApplicationContext {

    private final EventBroadcaster eventBroadcaster;

    private final SettingProvider settingProvider;

}
