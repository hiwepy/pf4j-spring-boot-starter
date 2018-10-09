/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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

import java.util.ArrayList;
import java.util.List;

import ro.fortsoft.pf4j.DevelopmentPluginClasspath;
import ro.fortsoft.pf4j.ExtensionFactory;
import ro.fortsoft.pf4j.JarPluginManager;
import ro.fortsoft.pf4j.PluginClasspath;
import ro.fortsoft.pf4j.spring.SpringExtensionFactory;

public class ExtendedJarPluginManager extends JarPluginManager {

	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();
	
	public ExtendedJarPluginManager() {
	}
	
	public ExtendedJarPluginManager(List<String> classesDirectories, List<String> libDirectories) {
		this.classesDirectories.addAll(classesDirectories);
		this.libDirectories.addAll(libDirectories);
	}

	@Override
	protected PluginClasspath createPluginClasspath() {
		return isDevelopment() ? new DevelopmentPluginClasspath()
				: new ExtendedPluginClasspath(getClassesDirectories(), getLibDirectories());
	}
	
	@Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

	public List<String> getClassesDirectories() {
		return classesDirectories;
	}

	public void setClassesDirectories(List<String> classesDirectories) {
		this.classesDirectories = classesDirectories;
	}

	public List<String> getLibDirectories() {
		return libDirectories;
	}

	public void setLibDirectories(List<String> libDirectories) {
		this.libDirectories = libDirectories;
	}

}
