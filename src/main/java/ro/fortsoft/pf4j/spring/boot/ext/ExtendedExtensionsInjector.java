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

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.spring.ExtensionsInjector;
import ro.fortsoft.pf4j.spring.boot.ext.registry.Pf4jDynamicControllerRegistry;
import ro.fortsoft.pf4j.spring.boot.ext.utils.InjectorUtils;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class ExtendedExtensionsInjector extends ExtensionsInjector {

	private static final Logger log = LoggerFactory.getLogger(ExtendedExtensionsInjector.class);
	
	protected PluginManager pluginManager;
    protected ConfigurableListableBeanFactory beanFactory;
    protected Pf4jDynamicControllerRegistry dynamicControllerRegistry;
    
	public ExtendedExtensionsInjector(PluginManager pluginManager, 
			Pf4jDynamicControllerRegistry dynamicControllerRegistry) {
		 this.pluginManager = pluginManager;
		 this.dynamicControllerRegistry = dynamicControllerRegistry;
	}
	
	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		this.beanFactory = beanFactory;
		//this.requestMappingHandlerMapping = beanFactory.getBean(RequestMappingHandlerMapping.class);
		
        // add extensions from classpath (non plugin)
        Set<String> extensionClassNames = pluginManager.getExtensionClassNames(null);
        for (String extensionClassName : extensionClassNames) {
            try {
                log.debug("Register extension '{}' as bean", extensionClassName);
                Class<?> extensionClass = getClass().getClassLoader().loadClass(extensionClassName);
                registerExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        
        // add extensions for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            log.debug("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
            extensionClassNames = pluginManager.getExtensionClassNames(plugin.getPluginId());
            for (String extensionClassName : extensionClassNames) {
                try {
                    log.debug("Register extension '{}' as bean", extensionClassName);
                    Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                    registerExtension(extensionClass);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
	
	/**
     * Register an extension as bean.
     * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
     * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
     * The bean name is the extension class name.
     * Override this method if you wish other register strategy.
     */
    protected void registerExtension(Class<?> extensionClass) {
    	
    	Object extension = pluginManager.getExtensionFactory().create(extensionClass);
		if(!InjectorUtils.isInjectNecessary(extensionClass)) {
			return;
		}
		String beanName = InjectorUtils.getBeanName(extensionClass, extension.getClass().getName());
		// 判断对象是否是Controller
		if (InjectorUtils.isController(extensionClass)) {
			dynamicControllerRegistry.registerController(beanName, extension);
		} else {
			beanFactory.registerSingleton(beanName, extension);
		}
		
    }
	
}
