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

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import ro.fortsoft.pf4j.PluginClasspath;
import ro.fortsoft.pf4j.PluginDescriptor;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginStateEvent;
import ro.fortsoft.pf4j.PluginStateListener;
import ro.fortsoft.pf4j.RuntimeMode;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jJarPluginManager;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jJarPluginWhitSpringManager;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jPluginClasspath;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jPluginManager;
import ro.fortsoft.pf4j.spring.boot.ext.Pf4jUpdateRepository;
import ro.fortsoft.pf4j.spring.boot.ext.PluginLazyTask;
import ro.fortsoft.pf4j.spring.boot.ext.PluginUtils;
import ro.fortsoft.pf4j.spring.boot.ext.PluginsLazyTask;
import ro.fortsoft.pf4j.update.DefaultUpdateRepository;
import ro.fortsoft.pf4j.update.UpdateManager;
import ro.fortsoft.pf4j.update.UpdateRepository;

/**
 * Pf4j 1.x Configuration
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@Configuration
@ConditionalOnClass({ PluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(Pf4jProperties.class)
public class Pf4jAutoConfiguration implements DisposableBean {

	private PluginManager pluginManager;
	private Logger logger = LoggerFactory.getLogger(Pf4jAutoConfiguration.class);
	// 实例化Timer类
	private Timer timer = new Timer(true);

	@Bean
	@ConditionalOnMissingBean(PluginStateListener.class)
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
	public PluginManager pluginManager(Pf4jProperties properties) {

		// 设置运行模式
		RuntimeMode mode = RuntimeMode.byName(properties.getMode());
		System.setProperty("pf4j.mode", mode.toString());

		// 设置插件目录
		String pluginsDir = StringUtils.hasText(properties.getPluginsDir()) ? properties.getPluginsDir() : "plugins";
		System.setProperty("pf4j.pluginsDir", pluginsDir);
		String apphome = System.getProperty("app.home");
		if (RuntimeMode.DEPLOYMENT.compareTo(RuntimeMode.byName(properties.getMode())) == 0
				&& StringUtils.hasText(apphome)) {
			System.setProperty("pf4j.pluginsDir", apphome + File.separator + pluginsDir);
		}

		// final PluginManager pluginManager = new DefaultPluginManager();
		// final PluginManager pluginManager = new JarPluginManager();

		PluginManager pluginManager = null;
		if (properties.isJarPackages()) {

			PluginClasspath pluginClasspath = new Pf4jPluginClasspath(properties.getClassesDirectories(),
					properties.getLibDirectories());

			if (properties.isSpring()) {

				/**
				 * 使用Spring时需编写如下的初始化逻辑
				 * 
				 * @Configuration public class Pf4jConfig {
				 * @Bean public ExtensionsInjector extensionsInjector() { return new
				 *       ExtensionsInjector(); } }
				 * 
				 */

				pluginManager = new Pf4jJarPluginWhitSpringManager(pluginClasspath);
			} else {
				pluginManager = new Pf4jJarPluginManager(pluginClasspath);
			}
		} else {
			pluginManager = new Pf4jPluginManager(pluginsDir);
		}

		/*
		 * pluginManager.enablePlugin(pluginId) pluginManager.disablePlugin(pluginId)
		 * pluginManager.deletePlugin(pluginId)
		 * 
		 * pluginManager.loadPlugin(pluginPath) pluginManager.startPlugin(pluginId)
		 * pluginManager.stopPlugin(pluginId) pluginManager.unloadPlugin(pluginId)
		 */

		if (properties.isLazy()) {
			// 延时加载、启动插件目录中的插件
			timer.schedule(new PluginLazyTask(pluginManager), properties.getDelay());
			// 延时加载、启动绝对路径指定的插件
			timer.schedule(new PluginsLazyTask(pluginManager, properties.getPlugins()), properties.getDelay());
		} else {

			// 加载、启动插件目录中的插件
			pluginManager.loadPlugins();
			/*
			 * 调用Plugin实现类的start()方法:
			 */
			pluginManager.startPlugins();
			// 加载、启动绝对路径指定的插件
			PluginUtils.loadAndStartPlugins(pluginManager, properties.getPlugins());
		}

		this.pluginManager = pluginManager;
		return pluginManager;
	}

	@Bean
	public UpdateManager updateManager(PluginManager pluginManager, Pf4jProperties properties) {
		UpdateManager updateManager = null;
		if (StringUtils.hasText(properties.getReposJsonPath())) {
			updateManager = new UpdateManager(pluginManager, Paths.get(properties.getReposJsonPath()));
		} else if (!CollectionUtils.isEmpty(properties.getRepos())) {

			List<UpdateRepository> repos = new ArrayList<UpdateRepository>();
			for (Pf4jUpdateRepository repo : properties.getRepos()) {
				repos.add(new DefaultUpdateRepository(repo.getId(), repo.getUrl(), repo.getPluginsJsonFileName()));
			}
			updateManager = new UpdateManager(pluginManager, repos);

		} else {
			updateManager = new UpdateManager(pluginManager);
		}
		return updateManager;

	}

	@Override
	public void destroy() throws Exception {
		// 销毁插件
		if (pluginManager != null) {
			/*
			 * 调用Plugin实现类的stop()方法
			 */
			pluginManager.stopPlugins();
		}

	}

}