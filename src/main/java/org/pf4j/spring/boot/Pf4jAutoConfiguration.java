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

import java.io.File;

import org.pf4j.PluginManager;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.extension.ExtendedSpringPluginManager;
import org.pf4j.spring.extension.registry.DynamicControllerRegistry;
import org.pf4j.spring.extension.registry.Pf4jDynamicControllerRegistry;
import org.pf4j.update.UpdateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Pf4j Spring Boot Auto Configuration.
 * <p>
 * Delegates bean construction to {@code pf4j-extension-spring} and only handles
 * Spring Boot property binding + auto-configuration wiring.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@Configuration
@AutoConfigureAfter({ Pf4jUpdateAutoConfiguration.class })
@ConditionalOnClass({ PluginManager.class, UpdateManager.class, ExtendedSpringPluginManager.class })
@ConditionalOnProperty(prefix = Pf4jProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({Pf4jProperties.class})
public class Pf4jAutoConfiguration {

	private Logger logger = LoggerFactory.getLogger(Pf4jAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean(DynamicControllerRegistry.class)
	public DynamicControllerRegistry pf4jDynamicControllerRegistry(
			ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider) {
		RequestMappingHandlerMapping handlerMapping = handlerMappingProvider.getIfAvailable();
		if (handlerMapping == null) {
			handlerMapping = new RequestMappingHandlerMapping();
		}
		return new Pf4jDynamicControllerRegistry(handlerMapping);
	}

	@Bean
	public PluginManager pluginManager(Pf4jProperties properties,
			DynamicControllerRegistry dynamicControllerRegistry) {

		// Run mode
		System.setProperty("pf4j.mode", properties.getRuntimeMode().toString());

		// Plugin directory
		String pluginsRoot = StringUtils.hasText(properties.getPluginsRoot()) ? properties.getPluginsRoot() : "plugins";
		System.setProperty("pf4j.pluginsDir", pluginsRoot);
		String apphome = System.getProperty("app.home");
		if (RuntimeMode.DEPLOYMENT.compareTo(properties.getRuntimeMode()) == 0
				&& StringUtils.hasText(apphome)) {
			System.setProperty("pf4j.pluginsDir", apphome + File.separator + pluginsRoot);
		}

		ExtendedSpringPluginManager pluginManager = new ExtendedSpringPluginManager(
				dynamicControllerRegistry,
				pluginsRoot,
				properties.isAutowire(),
				properties.isSingleton(),
				properties.isInjectable());

		pluginManager.setExactVersionAllowed(properties.isExactVersionAllowed());
		pluginManager.setSystemVersion(properties.getSystemVersion());

		return pluginManager;
	}

}