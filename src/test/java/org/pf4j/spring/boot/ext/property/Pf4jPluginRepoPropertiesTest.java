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
package org.pf4j.spring.boot.ext.property;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Pf4jPluginRepoProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("Pf4jPluginRepoProperties Tests")
class Pf4jPluginRepoPropertiesTest {

    @Test
    @DisplayName("Default constructor creates non-null instance with expected defaults")
    void testDefaultInstance() {
        Pf4jPluginRepoProperties props = new Pf4jPluginRepoProperties();
        assertThat(props).isNotNull();
        assertThat(props.getId()).isNull();
        assertThat(props.getUrl()).isNull();
        assertThat(props.getPluginsJsonFileName()).isEqualTo("plugins.json");
    }

    @Test
    @DisplayName("id getter/setter")
    void testIdField() {
        Pf4jPluginRepoProperties props = new Pf4jPluginRepoProperties();
        props.setId("central");
        assertThat(props.getId()).isEqualTo("central");
    }

    @Test
    @DisplayName("url getter/setter")
    void testUrlField() throws MalformedURLException {
        Pf4jPluginRepoProperties props = new Pf4jPluginRepoProperties();
        URL url = new URL("http://example.com/plugins");
        props.setUrl(url);
        assertThat(props.getUrl()).isEqualTo(url);
    }

    @Test
    @DisplayName("pluginsJsonFileName getter/setter")
    void testPluginsJsonFileNameField() {
        Pf4jPluginRepoProperties props = new Pf4jPluginRepoProperties();
        props.setPluginsJsonFileName("custom-plugins.json");
        assertThat(props.getPluginsJsonFileName()).isEqualTo("custom-plugins.json");
    }
}
