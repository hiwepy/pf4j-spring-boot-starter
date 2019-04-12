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

import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.spring.boot.ext.MavenClientTemplate;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pf4j.PluginDescriptor;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.update.DefaultUpdateRepository;
import ro.fortsoft.pf4j.update.FileDownloader;
import ro.fortsoft.pf4j.update.PluginInfo;
import ro.fortsoft.pf4j.update.PluginInfo.PluginRelease;
import ro.fortsoft.pf4j.update.UpdateRepository;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class MavenUpdateRepository implements UpdateRepository {
	
    private static final Logger logger = LoggerFactory.getLogger(DefaultUpdateRepository.class);
    private String id;
    private Map<String, PluginInfo> plugins;
	private PluginManager pluginManager;
	private MavenClientTemplate mavenClientTemplate;
	
	public MavenUpdateRepository(String id, MavenClientTemplate mavenClientTemplate, PluginManager pluginManager) {
		this.id = id;
		this.mavenClientTemplate = mavenClientTemplate;
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
    	
    	try {
			for (PluginWrapper installed : pluginManager.getPlugins()) {
				
				PluginInfo info = new PluginInfo();
				// 解析Maven版本信息
				VersionRangeResult versionRangeResult = mavenClientTemplate.versionResult(installed.getPluginId());
				
				PluginDescriptor descriptor = installed.getDescriptor();
				info.id = installed.getPluginId();
				info.description = descriptor.getPluginDescription(); 
				info.provider = descriptor.getProvider();
				info.releases = versionRangeResult.getVersions().stream().map(version -> {
					PluginRelease release = new PluginInfo.PluginRelease(); 
					release.version = version.toString();
					//release.date = new Date();
					return release;
				}).collect(Collectors.toList());
				
				plugins.put(installed.getPluginId(), info);
			}
      
			logger.debug("Found {} plugins in repository '{}'", plugins.size(), id);
			
		} catch (VersionRangeResolutionException e) {
			e.printStackTrace();
		}
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
        return new MavenFileDownloader(mavenClientTemplate);
    }

}
