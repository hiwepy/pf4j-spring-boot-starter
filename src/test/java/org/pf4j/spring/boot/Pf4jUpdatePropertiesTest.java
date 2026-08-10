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

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pf4j.spring.boot.ext.property.Pf4jPluginRepoProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Pf4jUpdateProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("Pf4jUpdateProperties Tests")
class Pf4jUpdatePropertiesTest {

    @Test
    @DisplayName("Default constructor creates non-null instance with expected defaults")
    void testDefaultInstance() {
        Pf4jUpdateProperties props = new Pf4jUpdateProperties();
        assertThat(props).isNotNull();
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.getReposJsonPath()).isNull();
        assertThat(props.getReposRestPath()).isNull();
        assertThat(props.getRepos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("enabled getter/setter")
    void testEnabledField() {
        Pf4jUpdateProperties props = new Pf4jUpdateProperties();
        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("reposJsonPath getter/setter")
    void testReposJsonPathField() {
        Pf4jUpdateProperties props = new Pf4jUpdateProperties();
        props.setReposJsonPath("/path/to/repos.json");
        assertThat(props.getReposJsonPath()).isEqualTo("/path/to/repos.json");
    }

    @Test
    @DisplayName("reposRestPath getter/setter")
    void testReposRestPathField() {
        Pf4jUpdateProperties props = new Pf4jUpdateProperties();
        props.setReposRestPath("http://example.com/repos");
        assertThat(props.getReposRestPath()).isEqualTo("http://example.com/repos");
    }

    @Test
    @DisplayName("repos getter/setter")
    void testReposField() {
        Pf4jUpdateProperties props = new Pf4jUpdateProperties();
        Pf4jPluginRepoProperties repo = new Pf4jPluginRepoProperties();
        repo.setId("central");
        props.setRepos(Collections.singletonList(repo));
        assertThat(props.getRepos()).hasSize(1);
        assertThat(props.getRepos().get(0).getId()).isEqualTo("central");
    }

    @Test
    @DisplayName("Public constant 'PREFIX' has expected value")
    void testPREFIXConstant() {
        assertThat(Pf4jUpdateProperties.PREFIX).isEqualTo("pf4j.update");
    }
}
