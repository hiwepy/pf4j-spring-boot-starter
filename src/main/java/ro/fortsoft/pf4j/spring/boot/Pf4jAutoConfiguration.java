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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.fortsoft.pf4j.PluginClasspath;
import ro.fortsoft.pf4j.PluginDescriptor;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginStateEvent;
import ro.fortsoft.pf4j.PluginStateListener;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jJarPluginManager;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jPluginClasspath;

/**
 * 
 * @className	： Pf4jAutoConfiguration
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年10月31日 下午6:25:53
 * @version 	V1.0
 */
@Configuration
@ConditionalOnClass({ PluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ Pf4jProperties.class })
public class Pf4jAutoConfiguration implements InitializingBean, DisposableBean {

	private PluginManager pluginManager;
	private Logger logger = LoggerFactory.getLogger(Pf4jAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean(PluginStateListener.class)
	@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "autowire", havingValue = "false", matchIfMissing = true)
	public PluginStateListener pluginStateListener() {

		return new PluginStateListener() {

			@Override
			public void pluginStateChanged(PluginStateEvent event) {
					
				PluginDescriptor descriptor = event.getPlugin().getDescriptor();

				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Plugin [%s（%s）](%s) %s", descriptor.getPluginId(),
							descriptor.getVersion().toString(), descriptor.getPluginDescription(),
							event.getPluginState().toString()));
				}

			}

		};
	}
	
	@Bean
	@ConditionalOnMissingBean(PluginManager.class)
	@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "autowire", havingValue = "false", matchIfMissing = true)
	public PluginManager pluginManager(Pf4jProperties properties) {
		// final PluginManager pluginManager = new DefaultPluginManager();
		// final PluginManager pluginManager = new JarPluginManager();
		
		//PluginManager pluginManager = new Pf4jPluginManager(properties);
		
		PluginClasspath pluginClasspath = new Pf4jPluginClasspath(properties.getClassesDirectories() , properties.getLibDirectories());
		
		PluginManager pluginManager = new Pf4jJarPluginManager(pluginClasspath);
		
		
		/*pluginManager.enablePlugin(pluginId)
		pluginManager.disablePlugin(pluginId)
		pluginManager.deletePlugin(pluginId)
		
		pluginManager.loadPlugin(pluginPath)
		pluginManager.startPlugin(pluginId)
		pluginManager.stopPlugin(pluginId)
		pluginManager.unloadPlugin(pluginId)*/
		
		//加载插件
		pluginManager.loadPlugins();
		
		// pluginManager.addPluginStateListener(listener);

		this.pluginManager = pluginManager;
		return pluginManager;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// 启动插件
		if (pluginManager != null) {
			pluginManager.startPlugins();
		}

	}

	@Override
	public void destroy() throws Exception {
		//销毁插件
		if (pluginManager != null) {
			pluginManager.stopPlugins();
		}

	}

}
