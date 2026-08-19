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
package org.pf4j.spring.boot.ext.property;

import java.net.URL;

/**
 * <p>Configuration properties for Pf4jPluginRepo.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class Pf4jPluginRepoProperties {

	private String id;
	private URL url;
	private String pluginsJsonFileName = "plugins.json";

    /**
     * <p>Returns the id.</p>
     * @return the get id
     */
	public String getId() {
		return id;
	}

    /**
     * <p>Sets the id.</p>
     * @param id
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * <p>Returns the url.</p>
     * @return the get url
     */
	public URL getUrl() {
		return url;
	}

    /**
     * <p>Sets the url.</p>
     * @param url
     */
	public void setUrl(URL url) {
		this.url = url;
	}

    /**
     * <p>Returns the plugins json file name.</p>
     * @return the get plugins json file name
     */
	public String getPluginsJsonFileName() {
		return pluginsJsonFileName;
	}

    /**
     * <p>Sets the plugins json file name.</p>
     * @param pluginsJsonFileName
     */
	public void setPluginsJsonFileName(String pluginsJsonFileName) {
		this.pluginsJsonFileName = pluginsJsonFileName;
	}

}
