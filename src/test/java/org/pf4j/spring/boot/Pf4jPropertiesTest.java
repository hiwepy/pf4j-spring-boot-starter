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
import org.pf4j.RuntimeMode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Pf4jProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("Pf4jProperties Tests")
class Pf4jPropertiesTest {

    @Test
    @DisplayName("Default constructor creates non-null instance with expected defaults")
    void testDefaultInstance() {
        Pf4jProperties props = new Pf4jProperties();
        assertThat(props).isNotNull();
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.isAutowire()).isTrue();
        assertThat(props.isExactVersionAllowed()).isFalse();
        assertThat(props.isInjectable()).isTrue();
        assertThat(props.isSingleton()).isTrue();
        assertThat(props.getClassesDirectories()).isNotNull().isEmpty();
        assertThat(props.getLibDirectories()).isNotNull().isEmpty();
        assertThat(props.getPlugins()).isNotNull().isEmpty();
        assertThat(props.getPluginsRoot()).isEqualTo("plugins");
        assertThat(props.getRuntimeMode()).isEqualTo(RuntimeMode.DEPLOYMENT);
        assertThat(props.getSystemVersion()).isEqualTo("0.0.0");
        assertThat(props.isJarPackages()).isTrue();
    }

    @Test
    @DisplayName("enabled getter/setter")
    void testEnabledField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();
        props.setEnabled(false);
        assertThat(props.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("autowire getter/setter")
    void testAutowireField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setAutowire(false);
        assertThat(props.isAutowire()).isFalse();
        props.setAutowire(true);
        assertThat(props.isAutowire()).isTrue();
    }

    @Test
    @DisplayName("exactVersionAllowed getter/setter")
    void testExactVersionAllowedField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setExactVersionAllowed(true);
        assertThat(props.isExactVersionAllowed()).isTrue();
    }

    @Test
    @DisplayName("injectable getter/setter")
    void testInjectableField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setInjectable(false);
        assertThat(props.isInjectable()).isFalse();
    }

    @Test
    @DisplayName("singleton getter/setter")
    void testSingletonField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setSingleton(false);
        assertThat(props.isSingleton()).isFalse();
    }

    @Test
    @DisplayName("classesDirectories getter/setter")
    void testClassesDirectoriesField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setClassesDirectories(Collections.singletonList("/tmp/classes"));
        assertThat(props.getClassesDirectories()).containsExactly("/tmp/classes");
    }

    @Test
    @DisplayName("libDirectories getter/setter")
    void testLibDirectoriesField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setLibDirectories(Collections.singletonList("/tmp/libs"));
        assertThat(props.getLibDirectories()).containsExactly("/tmp/libs");
    }

    @Test
    @DisplayName("runtimeMode getter/setter")
    void testRuntimeModeField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setRuntimeMode(RuntimeMode.DEVELOPMENT);
        assertThat(props.getRuntimeMode()).isEqualTo(RuntimeMode.DEVELOPMENT);
    }

    @Test
    @DisplayName("pluginsRoot getter/setter")
    void testPluginsRootField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setPluginsRoot("/custom/plugins");
        assertThat(props.getPluginsRoot()).isEqualTo("/custom/plugins");
    }

    @Test
    @DisplayName("plugins getter/setter")
    void testPluginsField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setPlugins(Collections.singletonList("my-plugin"));
        assertThat(props.getPlugins()).containsExactly("my-plugin");
    }

    @Test
    @DisplayName("jarPackages getter/setter")
    void testJarPackagesField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setJarPackages(false);
        assertThat(props.isJarPackages()).isFalse();
    }

    @Test
    @DisplayName("systemVersion getter/setter")
    void testSystemVersionField() {
        Pf4jProperties props = new Pf4jProperties();
        props.setSystemVersion("1.2.3");
        assertThat(props.getSystemVersion()).isEqualTo("1.2.3");
    }

    @Test
    @DisplayName("Public constant 'PREFIX' has expected value")
    void testPREFIXConstant() {
        assertThat(Pf4jProperties.PREFIX).isEqualTo("pf4j");
    }
}
