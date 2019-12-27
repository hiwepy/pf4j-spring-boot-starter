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
package ro.fortsoft.pf4j.spring.boot;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.spring.boot.ext.property.Pf4jPluginRepoProperties;
import ro.fortsoft.pf4j.spring.boot.ext.property.Pf4jUpdateMavenProperties;
import ro.fortsoft.pf4j.spring.boot.ext.update.DefaultPluginInfoProvider;
import ro.fortsoft.pf4j.spring.boot.ext.update.PluginInfoProvider;
import ro.fortsoft.pf4j.update.DefaultUpdateRepository;
import ro.fortsoft.pf4j.update.UpdateManager;
import ro.fortsoft.pf4j.update.UpdateRepository;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Configuration
@ConditionalOnClass({ UpdateManager.class })
@ConditionalOnProperty(prefix = Pf4jUpdateProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({Pf4jUpdateProperties.class, Pf4jUpdateMavenProperties.class})
public class Pf4jUpdateAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public PluginInfoProvider pluginInfoProvider() {
		return new DefaultPluginInfoProvider();
	}
	
	@Bean
	public UpdateManager updateManager(
			PluginManager pluginManager,
			@Autowired(required = false) ObjectProvider<UpdateRepository> repoProvider,
			Pf4jUpdateProperties properties) {
		
		List<UpdateRepository> repositories = new ArrayList<UpdateRepository>();
		if(!CollectionUtils.isEmpty(properties.getRepos())) {
			for (Pf4jPluginRepoProperties repo : properties.getRepos()) {
				repositories.add(new DefaultUpdateRepository(repo.getId(), repo.getUrl(), repo.getPluginsJsonFileName()));
			}
		}
		List<UpdateRepository> repos = repoProvider.orderedStream().collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(repos)) {
			for (UpdateRepository newRepo : repos) {
				if (newRepo != null) {
					repositories.add(newRepo);
				}
			}
		}
		
		UpdateManager updateManager = null;
		if (StringUtils.hasText(properties.getReposJsonPath())) {
			updateManager = new UpdateManager(pluginManager, Paths.get(properties.getReposJsonPath()));
			if(!CollectionUtils.isEmpty(repositories)) {
				updateManager.setRepositories(repositories);
			}
		} else if(!CollectionUtils.isEmpty(repositories)) {
			updateManager = new UpdateManager(pluginManager, repositories);
		} else {
			updateManager = new UpdateManager(pluginManager);
		}
		
		return updateManager;
	}
	
}
