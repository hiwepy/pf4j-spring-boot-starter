package ro.fortsoft.pf4j.spring.boot.ext;

import ro.fortsoft.pf4j.DefaultPluginClasspath;
import ro.fortsoft.pf4j.DevelopmentPluginClasspath;
import ro.fortsoft.pf4j.ExtensionFactory;
import ro.fortsoft.pf4j.JarPluginManager;
import ro.fortsoft.pf4j.PluginClasspath;
import ro.fortsoft.pf4j.spring.SpringExtensionFactory;

public class Pf4jJarPluginWhitSpringManager extends JarPluginManager {

	public Pf4jJarPluginWhitSpringManager() {
	}
	
	public Pf4jJarPluginWhitSpringManager(PluginClasspath pluginClasspath) {
		this.pluginClasspath = pluginClasspath;
	}
	
	@Override
	protected PluginClasspath createPluginClasspath() {
		if(this.pluginClasspath != null) {
			return pluginClasspath;
		}
		return isDevelopment() ? new DevelopmentPluginClasspath() : new DefaultPluginClasspath();
    }
	
	@Override
	protected ExtensionFactory createExtensionFactory() {
		return new SpringExtensionFactory(this);
	}
	
}
