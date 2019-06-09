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
package ro.fortsoft.pf4j.spring.boot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.zafarkhaja.semver.Version;

import ro.fortsoft.pf4j.PluginException;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.update.PluginInfo;
import ro.fortsoft.pf4j.update.UpdateManager;

@EnableScheduling
@SpringBootApplication
public class Pf4jUpdateAutoConfiguration_Test {

	private Logger logger = LoggerFactory.getLogger(Pf4jUpdateAutoConfiguration_Test.class);
	@Autowired
	private PluginManager pluginManager = null;
	@Autowired
	private UpdateManager updateManager = null;
	
	/**
	 * 每10分钟更新一次
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void autoUpdate() {
		
		// >> keep system up-to-date <<
	    boolean systemUpToDate = true;
	    
	    // check for updates
	    if (updateManager.hasUpdates()) {
	        List<PluginInfo> updates = updateManager.getUpdates();
	        logger.debug("Found {} updates", updates.size());
	        for (PluginInfo plugin : updates) {
	        	
	        	logger.debug("Found update for plugin '{}'", plugin.id);
	            PluginInfo.PluginRelease lastRelease = plugin.getLastRelease(pluginManager.getSystemVersion());
	            String lastVersion = lastRelease.version;
	            Version installedVersion = pluginManager.getPlugin(plugin.id).getDescriptor().getVersion();
	            logger.debug("Update plugin '{}' from version {} to version {}", plugin.id, installedVersion, lastVersion);
				try {
					boolean updated = updateManager.updatePlugin(plugin.id, lastVersion);
					if (updated) {
		            	logger.debug("Updated plugin '{}'", plugin.id);
		            } else {
		            	logger.error("Cannot update plugin '{}'", plugin.id);
		                systemUpToDate = false;
		            }
				} catch (PluginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }
	    } else {
	    	logger.debug("No updates found");
	    }

	    // Check for available (new) plugins
	    if (updateManager.hasAvailablePlugins()) {
	        List<PluginInfo> availablePlugins = updateManager.getAvailablePlugins();
	        logger.debug("Found {} available plugins", availablePlugins.size());
	        for (PluginInfo plugin : availablePlugins) {
	        	logger.debug("Found available plugin '{}'", plugin.id);
	        	PluginInfo.PluginRelease lastRelease = plugin.getLastRelease(pluginManager.getSystemVersion());
	            String lastVersion = lastRelease.version;
	            logger.debug("Install plugin '{}' with version {}", plugin.id, lastVersion);
	            try {
		            boolean installed = updateManager.installPlugin(plugin.id, lastVersion);
		            if (installed) {
		            	logger.debug("Installed plugin '{}'", plugin.id);
		            } else {
		            	logger.error("Cannot install plugin '{}'", plugin.id);
		                systemUpToDate = false;
		            }
	            } catch (PluginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    } else {
	    	logger.debug("No available plugins found");
	    }

	    if (systemUpToDate) {
	    	logger.debug("System up-to-date");
	    }
		
	}
	
	
}
