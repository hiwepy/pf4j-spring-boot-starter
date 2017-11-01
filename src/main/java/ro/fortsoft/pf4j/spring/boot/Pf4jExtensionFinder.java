/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */
package ro.fortsoft.pf4j;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pf4j.AbstractExtensionFinder;
import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.ExtensionDescriptor;
import ro.fortsoft.pf4j.ExtensionWrapper;
import ro.fortsoft.pf4j.LegacyExtensionFinder;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginState;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;
import ro.fortsoft.pf4j.util.ClassUtils;

public class Pf4jExtensionFinder extends LegacyExtensionFinder {

	private static final Logger log = LoggerFactory.getLogger(AbstractExtensionFinder.class);

	public Pf4jExtensionFinder(PluginManager pluginManager) {
		super(pluginManager);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<ExtensionWrapper<T>> find(Class<T> type, String pluginId) {
		log.debug("Finding extensions of extension point '{}' for plugin '{}'", type.getName(), pluginId);
		List<ExtensionWrapper<T>> result = new ArrayList<>();

		// classpath's extensions <=> pluginId = null
		Set<String> classNames = findClassNames(pluginId);
		if (classNames == null || classNames.isEmpty()) {
			return result;
		}

		if (pluginId != null) {
			PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
			if (PluginState.STARTED != pluginWrapper.getPluginState()) {
				return result;
			}

			log.trace("Checking extensions from plugin '{}'", pluginId);
		} else {
			log.trace("Checking extensions from classpath");
		}

		ClassLoader classLoader = (pluginId != null) ? pluginManager.getPluginClassLoader(pluginId)
				: getClass().getClassLoader();

		for (String className : classNames) {
			try {
				log.debug("Loading class '{}' using class loader '{}'", className, classLoader);
				Class<?> extensionClass = classLoader.loadClass(className);

				log.debug("Checking extension type '{}'", className);
				if (type.isAssignableFrom(extensionClass)) {
					ExtensionWrapper extensionWrapper = createExtensionWrapper(extensionClass);
					result.add(extensionWrapper);
					log.debug("Added extension '{}' with ordinal {}", className, extensionWrapper.getOrdinal());
				} else {
					log.trace("'{}' is not an extension for extension point '{}'", className, type.getName());
					if (RuntimeMode.DEVELOPMENT.equals(pluginManager.getRuntimeMode())) {
						checkDifferentClassLoaders(type, extensionClass);
					}
				}
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}

		if (result.isEmpty()) {
			log.debug("No extensions found for extension point '{}'", type.getName());
		} else {
			log.debug("Found {} extensions for extension point '{}'", result.size(), type.getName());
		}

		// sort by "ordinal" property
		Collections.sort(result);

		return result;
	}
	

    private ExtensionWrapper createExtensionWrapper(Class<?> extensionClass) {
        ExtensionDescriptor descriptor = new ExtensionDescriptor();
        int ordinal = 0;
        if (extensionClass.isAnnotationPresent(Extension.class)) {
            ordinal = extensionClass.getAnnotation(Extension.class).ordinal();
        }
        descriptor.setOrdinal(ordinal);
        descriptor.setExtensionClass(extensionClass);

        ExtensionWrapper extensionWrapper = new ExtensionWrapper<>(descriptor);
        extensionWrapper.setExtensionFactory(pluginManager.getExtensionFactory());

        return extensionWrapper;
    }

    private void checkDifferentClassLoaders(Class<?> type, Class<?> extensionClass) {
        ClassLoader typeClassLoader = type.getClassLoader(); // class loader of extension point
        ClassLoader extensionClassLoader = extensionClass.getClassLoader();
        boolean match = ClassUtils.getAllInterfacesNames(extensionClass).contains(type.getSimpleName());
        if (match && !extensionClassLoader.equals(typeClassLoader)) {
            // in this scenario the method 'isAssignableFrom' returns only FALSE
            // see http://www.coderanch.com/t/557846/java/java/FWIW-FYI-isAssignableFrom-isInstance-differing
            log.error("Different class loaders: '{}' (E) and '{}' (EP)", extensionClassLoader, typeClassLoader);
        }
    }

}
