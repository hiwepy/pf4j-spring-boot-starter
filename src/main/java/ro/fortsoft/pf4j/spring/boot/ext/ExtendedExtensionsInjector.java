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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ro.fortsoft.pf4j.ExtensionFactory;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.spring.ExtensionsInjector;
import ro.fortsoft.pf4j.spring.boot.ext.utils.InjectorUtils;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class ExtendedExtensionsInjector extends ExtensionsInjector {

	private static final Logger log = LoggerFactory.getLogger(ExtendedExtensionsInjector.class);

	// RequestMappingHandlerMapping
	protected static Method detectHandlerMethodsMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"detectHandlerMethods", Object.class);
	protected static Method getMappingForMethodMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
			"getMappingForMethod", Method.class, Class.class);

	protected static Field mappingRegistryField = ReflectionUtils.findField(RequestMappingHandlerMapping.class,
			"mappingRegistry");

	protected static Field injectionMetadataCacheField = ReflectionUtils
			.findField(AutowiredAnnotationBeanPostProcessor.class, "injectionMetadataCache");

	static {
		detectHandlerMethodsMethod.setAccessible(true);
		getMappingForMethodMethod.setAccessible(true);
		mappingRegistryField.setAccessible(true);
		injectionMetadataCacheField.setAccessible(true);
	}
	
	protected RequestMappingHandlerMapping requestMappingHandlerMapping;
	protected final PluginManager pluginManager;
    protected ConfigurableListableBeanFactory beanFactory;

	public ExtendedExtensionsInjector(PluginManager pluginManager, AbstractAutowireCapableBeanFactory beanFactory) {
		 this.pluginManager = pluginManager;
		 this.requestMappingHandlerMapping = beanFactory.getBean(RequestMappingHandlerMapping.class);
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		this.beanFactory = beanFactory;

		PluginManager pluginManager = beanFactory.getBean(PluginManager.class);
		ExtensionFactory extensionFactory = pluginManager.getExtensionFactory();

		// add extensions from classpath (non plugin)
		Set<String> extensionClassNames = pluginManager.getExtensionClassNames(null);
		for (String extensionClassName : extensionClassNames) {
			try {
				log.debug("Register extension '{}' as bean", extensionClassName);
				Class<?> extensionClass = getClass().getClassLoader().loadClass(extensionClassName);
				Object bean = extensionFactory.create(extensionClass);
				String beanName = InjectorUtils.getBeanName(bean, extensionClassName);
				
				// 判断对象是否是Controller
				if (InjectorUtils.isController(bean)) {
					// 1、如果RequestMapping存在则移除
					removeRequestMappingIfNecessary(beanName);
					// 2、注册新的Controller
					beanFactory.registerSingleton(beanName, bean);
					// 3、注册新的RequestMapping
					registerRequestMappingIfNecessary(beanName);
				} else {
					beanFactory.registerSingleton(beanName, bean);
				}
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
					Object bean = extensionFactory.create(extensionClass);
					String beanName = InjectorUtils.getBeanName(bean, extensionClassName);
					
					// 判断对象是否是Controller
					if (InjectorUtils.isController(bean)) {
						// 1、如果RequestMapping存在则移除
						removeRequestMappingIfNecessary(beanName);
						// 2、注册新的Controller
						beanFactory.registerSingleton(beanName, bean);
						// 3、注册新的RequestMapping
						registerRequestMappingIfNecessary(beanName);
					} else {
						beanFactory.registerSingleton(beanName, bean);
					}
				} catch (ClassNotFoundException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void removeRequestMappingIfNecessary(String controllerBeanName) {

		if (!beanFactory.containsBean(controllerBeanName)) {
			return;
		}

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();

		// remove old
		Class<?> handlerType = beanFactory.getType(controllerBeanName);
		final Class<?> userType = ClassUtils.getUserClass(handlerType);

		/*
		 * Map<RequestMappingInfo, HandlerMethod> handlerMethods =
		 * requestMappingHandlerMapping.getHandlerMethods(); 返回只读集合：
		 * 特别说明：因requestMappingHandlerMapping.getHandlerMethods()方法获取到的结果是只读集合，
		 * 不能进行移除操作，所以需要采用反射方式获取目标对象
		 */
		Object mappingRegistry = ReflectionUtils.getField(mappingRegistryField, requestMappingHandlerMapping);
		Method getMappingsMethod = ReflectionUtils.findMethod(mappingRegistry.getClass(), "getMappings");
		getMappingsMethod.setAccessible(true);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = (Map<RequestMappingInfo, HandlerMethod>) ReflectionUtils
				.invokeMethod(getMappingsMethod, mappingRegistry);

		/*
		 * 查找URL映射：解决 Ambiguous handler methods mapped for HTTP path “” 问题
		 */
		Field urlLookupField = ReflectionUtils.findField(mappingRegistry.getClass(), "urlLookup");
		urlLookupField.setAccessible(true);
		MultiValueMap<String, RequestMappingInfo> urlMapping = (MultiValueMap<String, RequestMappingInfo>) ReflectionUtils
				.getField(urlLookupField, mappingRegistry);

		final RequestMappingHandlerMapping innerRequestMappingHandlerMapping = requestMappingHandlerMapping;
		Set<Method> methods = MethodIntrospector.selectMethods(userType, new ReflectionUtils.MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return ReflectionUtils.invokeMethod(getMappingForMethodMethod, innerRequestMappingHandlerMapping,
						method, userType) != null;
			}
		});

		for (Method method : methods) {

			RequestMappingInfo requestMappingInfo = (RequestMappingInfo) ReflectionUtils
					.invokeMethod(getMappingForMethodMethod, requestMappingHandlerMapping, method, userType);

			handlerMethods.remove(requestMappingInfo);

			PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
			Set<String> patterns = patternsCondition.getPatterns();
			// (Set<String>)
			// ReflectionUtils.invokeMethod(getMappingPathPatternsMethod,
			// requestMappingHandlerMapping, mapping);

			PathMatcher pathMatcher = requestMappingHandlerMapping.getPathMatcher();
			// (PathMatcher) ReflectionUtils.invokeMethod(getPathMatcherMethod,
			// requestMappingHandlerMapping);

			for (String pattern : patterns) {
				if (!pathMatcher.isPattern(pattern)) {
					urlMapping.remove(pattern);
				}
			}
		}

	}

	protected void registerRequestMappingIfNecessary(String controllerBeanName) {

		RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping();
		// spring 3.1 开始
		ReflectionUtils.invokeMethod(detectHandlerMethodsMethod, requestMappingHandlerMapping, controllerBeanName);

	}

	protected RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		try {
			if (requestMappingHandlerMapping != null) {
				return requestMappingHandlerMapping;
			}
			return beanFactory.getBean(RequestMappingHandlerMapping.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("applicationContext must has RequestMappingHandlerMapping");
		}
	}
	
}
