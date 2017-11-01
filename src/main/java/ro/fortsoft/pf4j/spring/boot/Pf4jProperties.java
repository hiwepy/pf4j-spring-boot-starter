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
package ro.fortsoft.pf4j.spring.boot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ro.fortsoft.pf4j.RuntimeMode;

/**
 * 
 * @className	： Pf4jProperties
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年10月31日 下午6:24:59
 * @version 	V1.0
 */
@ConfigurationProperties(prefix = Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "pf4j";

	/** 是否启用 **/
	protected boolean enabled = false;
	/** 数据库列与表达式对应关系 **/
	protected List<String> classesDirectories = new ArrayList<String>();
	protected List<String> libDirectories = new ArrayList<String>();
	/** 运行模式：development、 deployment **/
	protected String mode = RuntimeMode.DEPLOYMENT.toString();
	/** 插件目录：默认 plugins **/
	protected String pluginsDir = "plugins";
	/** 是否注册插件到Spring上下文 **/
	protected boolean spring = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPluginsDir() {
		return pluginsDir;
	}

	public void setPluginsDir(String pluginsDir) {
		this.pluginsDir = pluginsDir;
	}

	public boolean isSpring() {
		return spring;
	}

	public void setSpring(boolean spring) {
		this.spring = spring;
	}
	
}
