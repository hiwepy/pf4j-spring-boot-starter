/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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


import ro.fortsoft.pf4j.DefaultPluginClasspath;
import ro.fortsoft.pf4j.DevelopmentPluginClasspath;
import ro.fortsoft.pf4j.JarPluginManager;
import ro.fortsoft.pf4j.PluginClasspath;

public class Pf4jJarPluginManager extends JarPluginManager {

	public Pf4jJarPluginManager() {
	}
	
	public Pf4jJarPluginManager(PluginClasspath pluginClasspath) {
		this.pluginClasspath = pluginClasspath;
	}
	
	@Override
	protected PluginClasspath createPluginClasspath() {
		if(this.pluginClasspath != null) {
			return pluginClasspath;
		}
		return isDevelopment() ? new DevelopmentPluginClasspath() : new DefaultPluginClasspath();
    }
	
}
