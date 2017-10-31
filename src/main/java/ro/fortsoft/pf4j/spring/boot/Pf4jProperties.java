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

/**
 * 
 * @className	： Pf4jProperties
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年10月31日 下午6:24:59
 * @version 	V1.0
 */
@ConfigurationProperties(Pf4jProperties.PREFIX)
public class Pf4jProperties {

	public static final String PREFIX = "spring.pf4j";

	/** 是否启用 **/
	protected Boolean enabled = false;
	/** 数据库列与表达式对应关系 **/
	private List<String> classesDirectories = new ArrayList<String>();
	private List<String> libDirectories = new ArrayList<String>();
	/** 是否自动注入依赖对象,该参数需要 pf4j-spring 支持 **/
	private boolean autowire;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
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

	public boolean isAutowire() {
		return autowire;
	}

	public void setAutowire(boolean autowire) {
		this.autowire = autowire;
	}
	

}
