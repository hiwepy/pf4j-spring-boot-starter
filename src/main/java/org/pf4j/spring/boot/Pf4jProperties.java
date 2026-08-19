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

import java.util.ArrayList;
import java.util.List;

import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "pf4j";

	/** Enable Pf4j. */
	private boolean enabled = false;
	/** Whether to automatically inject dependent objects */
	private boolean autowire = true;
	/**
	 * Set to true to allow requires expression to be exactly x.y.z. The default is
	 * false, meaning that using an exact version x.y.z will implicitly mean the
	 * same as  &gt;=x.y.z
	 */
	private boolean exactVersionAllowed = false;
	/** Whether to register the object to the spring context */
	private boolean injectable = true;
	/** Whether always returns a singleton instance. */
	private boolean singleton = true;
	/** Extended Plugin Class Directory **/
	private List<String> classesDirectories = new ArrayList<String>();
	/** Extended Plugin Jar Directory **/
	private List<String> libDirectories = new ArrayList<String>();
	/** Runtime Mode：development、 deployment **/
	private RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;
	/**
	 * Plugin root directory: default “plugins”; when non-jar mode plugin, the value
	 * should be an absolute directory address
	 **/
	private String pluginsRoot = "plugins";
	/** Plugin address: absolute address **/
	private List<String> plugins = new ArrayList<String>();
	/** Whether the plugin is a JAR package **/
	private boolean jarPackages = true;
	/* The system version used for comparisons to the plugin requires attribute. */
	private String systemVersion = "0.0.0";
	
	
    /**
     * <p>Checks if enabled.</p>
     * @return the is enabled
     */
	public boolean isEnabled() {
		return enabled;
	}

    /**
     * <p>Sets the enabled.</p>
     * @param enabled
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    /**
     * <p>Checks if autowire.</p>
     * @return the is autowire
     */
	public boolean isAutowire() {
		return autowire;
	}

    /**
     * <p>Sets the autowire.</p>
     * @param autowire
     */
	public void setAutowire(boolean autowire) {
		this.autowire = autowire;
	}

    /**
     * <p>Checks if injectable.</p>
     * @return the is injectable
     */
	public boolean isInjectable() {
		return injectable;
	}

    /**
     * <p>Sets the injectable.</p>
     * @param injectable
     */
	public void setInjectable(boolean injectable) {
		this.injectable = injectable;
	}

    /**
     * <p>Checks if singleton.</p>
     * @return the is singleton
     */
	public boolean isSingleton() {
		return singleton;
	}

    /**
     * <p>Sets the singleton.</p>
     * @param singleton
     */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

    /**
     * <p>Returns the classes directories.</p>
     * @return the get classes directories
     */
	public List<String> getClassesDirectories() {
		return classesDirectories;
	}

    /**
     * <p>Sets the classes directories.</p>
     * @param classesDirectories
     */
	public void setClassesDirectories(List<String> classesDirectories) {
		this.classesDirectories = classesDirectories;
	}

    /**
     * <p>Returns the lib directories.</p>
     * @return the get lib directories
     */
	public List<String> getLibDirectories() {
		return libDirectories;
	}

    /**
     * <p>Sets the lib directories.</p>
     * @param libDirectories
     */
	public void setLibDirectories(List<String> libDirectories) {
		this.libDirectories = libDirectories;
	}

    /**
     * <p>Returns the runtime mode.</p>
     * @return the get runtime mode
     */
	public RuntimeMode getRuntimeMode() {
		return runtimeMode;
	}

    /**
     * <p>Sets the runtime mode.</p>
     * @param runtimeMode
     */
	public void setRuntimeMode(RuntimeMode runtimeMode) {
		this.runtimeMode = runtimeMode;
	}

    /**
     * <p>Returns the system version.</p>
     * @return the get system version
     */
	public String getSystemVersion() {
		return systemVersion;
	}

    /**
     * <p>Sets the system version.</p>
     * @param systemVersion
     */
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

    /**
     * <p>Returns the plugins root.</p>
     * @return the get plugins root
     */
	public String getPluginsRoot() {
		return pluginsRoot;
	}

    /**
     * <p>Sets the plugins root.</p>
     * @param pluginsRoot
     */
	public void setPluginsRoot(String pluginsRoot) {
		this.pluginsRoot = pluginsRoot;
	}

    /**
     * <p>Returns the plugins.</p>
     * @return the get plugins
     */
	public List<String> getPlugins() {
		return plugins;
	}

    /**
     * <p>Sets the plugins.</p>
     * @param plugins
     */
	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

    /**
     * <p>Checks if jar packages.</p>
     * @return the is jar packages
     */
	public boolean isJarPackages() {
		return jarPackages;
	}

    /**
     * <p>Sets the jar packages.</p>
     * @param jarPackages
     */
	public void setJarPackages(boolean jarPackages) {
		this.jarPackages = jarPackages;
	}

	/**
	 * Set to true to allow requires expression to be exactly x.y.z. The default is
	 * false, meaning that using an exact version x.y.z will implicitly mean the
	 * same as  &gt;=x.y.z
	 *
	 * @param exactVersionAllowed set to true or false
	 */
	public void setExactVersionAllowed(boolean exactVersionAllowed) {
		this.exactVersionAllowed = exactVersionAllowed;
	}

    /**
     * <p>Checks if exact version allowed.</p>
     * @return the is exact version allowed
     */
	public boolean isExactVersionAllowed() {
		return exactVersionAllowed;
	}

}
