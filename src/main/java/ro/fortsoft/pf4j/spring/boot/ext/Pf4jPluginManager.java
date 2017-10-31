/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
package ro.fortsoft.pf4j.spring.boot.ext;

import java.nio.file.Path;

	
import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.DevelopmentPluginClasspath;
import ro.fortsoft.pf4j.PluginClasspath;
import ro.fortsoft.pf4j.spring.boot.Pf4jProperties;

public class Pf4jPluginManager extends DefaultPluginManager {

	protected Pf4jProperties properties;

	public Pf4jPluginManager(Pf4jProperties properties) {
		super();
		this.properties = properties;
	}

	public Pf4jPluginManager(Path pluginsRoot) {
		super(pluginsRoot);
	}

	@Override
	protected PluginClasspath createPluginClasspath() {
		return isDevelopment() ? new DevelopmentPluginClasspath()
				: new Pf4jPluginClasspath(properties.getClassesDirectories(), properties.getLibDirectories());
	}

}
