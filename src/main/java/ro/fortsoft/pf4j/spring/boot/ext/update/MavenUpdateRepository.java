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
package ro.fortsoft.pf4j.spring.boot.ext.update;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenResource;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ro.fortsoft.pf4j.PluginException;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.spring.boot.ext.task.PluginUpdateTask;
import ro.fortsoft.pf4j.update.DefaultUpdateRepository;
import ro.fortsoft.pf4j.update.FileDownloader;
import ro.fortsoft.pf4j.update.PluginInfo;
import ro.fortsoft.pf4j.update.PluginInfo.PluginRelease;
import ro.fortsoft.pf4j.update.UpdateRepository;
import ro.fortsoft.pf4j.update.util.LenientDateTypeAdapter;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class MavenUpdateRepository implements UpdateRepository {
	
    private static final Logger logger = LoggerFactory.getLogger(DefaultUpdateRepository.class);
    private String id;
    private Map<String, PluginInfo> plugins;
	private MavenProperties properties;
	private PluginManager pluginManager;
	
	public MavenUpdateRepository(String id, MavenProperties properties, PluginManager pluginManager) {
		this.id = id;
		this.properties = properties;
		this.pluginManager = pluginManager;
	}
	
	@Override
    public String getId() {
        return id;
    }

	@Override
	public URL getUrl() {
		return null;
	}
	
	@Override
    public Map<String, PluginInfo> getPlugins() {
        if (plugins == null) {
            initPlugins();
        }
        //MavenResource.parse(coordinates, properties).getFile();
        return plugins;
    }
	
    @Override
    public PluginInfo getPlugin(String coordinates) {
        return getPlugins().get(coordinates);
    }

    private void initPlugins() {
    	
    	for (PluginWrapper installed : pluginManager.getPlugins()) {
    		
    		PluginInfo info = new PluginInfo();
    		
                Version installedVersion = installed.getDescriptor().getVersion();
                if (pluginFromRepo.hasUpdate(getSystemVersion(), installedVersion)) {
                    updates.add(pluginFromRepo);
                }
        }
    	pluginManager.getPlugins();
    	
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

    	
    	
        Reader pluginsJsonReader;
        try {
            URL pluginsUrl = new URL(getUrl(), pluginsJsonFileName);
            logger.debug("Read plugins of '{}' repository from '{}'", id, pluginsUrl);
            pluginsJsonReader = new InputStreamReader(pluginsUrl.openStream());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            plugins = Collections.emptyMap();
            return;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new LenientDateTypeAdapter()).create();
        PluginInfo[] items = gson.fromJson(pluginsJsonReader, PluginInfo[].class);
        plugins = new HashMap<>(items.length);
        for (PluginInfo p : items) {
            for (PluginRelease r : p.releases) {
                try {
                    r.url = new URL(getUrl(), r.url).toString();
                    if (r.date.getTime() == 0) {
                        logger.warn("Illegal release date when parsing {}@{}, setting to epoch", p.id, r.version);
                    }
                } catch (MalformedURLException e) {
                    logger.warn("Skipping release {} of plugin {} due to failure to build valid absolute URL. Url was {}{}", r.version, p.id, getUrl(), r.url);
                }
            }
            p.setRepositoryId(getId());
            plugins.put(p.id, p);
        }
        logger.debug("Found {} plugins in repository '{}'", plugins.size(), id);
    }

    /**
     * Causes plugins.json to be read again to look for new updates from repos
     */
    @Override
    public void refresh() {
        plugins = null;
    }

    @Override
    public FileDownloader getFileDownloader() {
        return new MavenFileDownloader(properties);
    }

}
