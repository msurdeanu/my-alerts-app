package org.myalerts.app.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "settings")
@Data
@Validated
public class SettingsConfig {

    private TestScenarioScheduler testScenarioScheduler = new TestScenarioScheduler();

    @Data
    public static class TestScenarioScheduler {
        @Min(1) @Max(8)
        private final int poolSize = 2;

        private final String threadNamePrefix = "test-scenario-pool-";
    }

}
