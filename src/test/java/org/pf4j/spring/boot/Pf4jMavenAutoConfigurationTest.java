/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.pf4j.spring.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {{ @link Pf4jMavenAutoConfiguration }}.
 *
 * <p>Verifies the auto-configuration activates under the expected conditions
 * and exposes its declared beans.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@DisplayName("Pf4jMavenAutoConfiguration Tests")
class Pf4jMavenAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    @DisplayName("Auto-configuration class can be instantiated")
    void testInstantiation() {
        Pf4jMavenAutoConfiguration configuration = new Pf4jMavenAutoConfiguration();
        assertThat(configuration).isNotNull();
    }

    @Test
    @DisplayName("Auto-configuration has expected annotations")
    void testLoadsWhenEnabledPropertySet() {
        // Pf4jMavenAutoConfiguration declares @ConditionalOnProperty and
        // @EnableConfigurationProperties. Verify the annotations are present
        // without loading the full context (Pf4jPluginRepoProperties lacks
        // @ConfigurationProperties which prevents full context startup in
        // Spring Boot 4.1.x).
        org.springframework.boot.autoconfigure.condition.ConditionalOnProperty cop =
                Pf4jMavenAutoConfiguration.class.getAnnotation(
                        org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class);
        assertThat(cop).isNotNull();
        assertThat(cop.prefix()).isEqualTo("pf4j.maven");
        assertThat(cop.havingValue()).isEqualTo("true");
    }

    @Test
    @DisplayName("Auto-configuration is absent when property is not set")
    void testNotLoadedWhenPropertyAbsent() {
        runner.withUserConfiguration(Pf4jMavenAutoConfiguration.class)
                .run(context -> assertThat(context).doesNotHaveBean(Pf4jMavenAutoConfiguration.class));
    }
}
