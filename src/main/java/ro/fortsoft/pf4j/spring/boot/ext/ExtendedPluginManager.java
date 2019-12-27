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
package ro.fortsoft.pf4j.spring.boot.ext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import ro.fortsoft.pf4j.DefaultExtensionFactory;
import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.ExtensionFactory;
import ro.fortsoft.pf4j.spring.SingletonSpringExtensionFactory;
import ro.fortsoft.pf4j.spring.SpringExtensionFactory;

public class ExtendedPluginManager extends DefaultPluginManager {

	/** Whether to automatically inject dependent objects */
	private boolean autowire = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	
	public ExtendedPluginManager(File pluginsRoot, boolean autowire, boolean singleton ) {
		super(pluginsRoot.toPath());
		this.autowire = autowire;
		this.singleton = singleton;
	}

	public ExtendedPluginManager(String pluginsRoot, boolean autowire, boolean singleton ) {
		super(Paths.get(pluginsRoot));
		this.autowire = autowire;
		this.singleton = singleton;
	}

	public ExtendedPluginManager(Path pluginsRoot, boolean autowire, boolean singleton ) {
		super(pluginsRoot);
		this.autowire = autowire;
		this.singleton = singleton;
	}

	@Override
    protected ExtensionFactory createExtensionFactory() {
		if (this.isAutowire()) {
			if (this.isSingleton()) {
				return new SingletonSpringExtensionFactory(this, true);
			}
			return new SpringExtensionFactory(this, true);
		}
		return new DefaultExtensionFactory();
    }
	
	public boolean isAutowire() {
		return autowire;
	}

	public boolean isSingleton() {
		return singleton;
	}

}
