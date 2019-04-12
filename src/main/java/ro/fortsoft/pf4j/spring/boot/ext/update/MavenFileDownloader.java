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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.apache.maven.spring.boot.ext.MavenClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pf4j.PluginException;
import ro.fortsoft.pf4j.update.FileDownloader;
import ro.fortsoft.pf4j.update.SimpleFileDownloader;

public class MavenFileDownloader implements FileDownloader {

    private static final Logger log = LoggerFactory.getLogger(SimpleFileDownloader.class);
	private MavenClientTemplate mavenClientTemplate;
	
	public MavenFileDownloader(MavenClientTemplate mavenClientTemplate) {
		this.mavenClientTemplate = mavenClientTemplate;
	}

	/**
     * Downloads a file. If HTTP(S) or FTP, stream content, if local file:/ do a simple filesystem copy to tmp folder.
     * Other protocols not supported.
     * @param fileUrl the URI representing the file to download
     * @return the path of downloaded/copied file
     * @throws IOException in case of network or IO problems
     * @throws PluginException in case of other problems
     */
    public Path downloadFile(URL fileUrl) throws PluginException, IOException {
        switch (fileUrl.getProtocol()) {
            case "http":
            case "https":
                return downloadFileHttp(fileUrl);
            default:
                throw new PluginException("URL protocol {} not supported", fileUrl.getProtocol());
        }
    }
    
    /**
     * Downloads file from HTTP or FTP
     * @param fileUrl source file
     * @return path of downloaded file
     * @throws IOException if IO problems
     * @throws PluginException if validation fails or any other problems
     */
    protected Path downloadFileHttp(URL fileUrl) throws IOException, PluginException {
        Path destination = Files.createTempDirectory("pf4j-update-downloader");
        destination.toFile().deleteOnExit();

        String path = fileUrl.getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        Path file = destination.resolve(fileName);


    	mavenClientTemplate.resolve(fileUrl.getFile()).getFile();
    	
    	
        // set up the URL connection
        URLConnection connection = fileUrl.openConnection();

        // connect to the remote site (may takes some time)
        connection.connect();

        // check for http authorization
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new ConnectException("HTTP Authorization failure");
        }

        // try to get the server-specified last-modified date of this artifact
        long lastModified = httpConnection.getHeaderFieldDate("Last-Modified", System.currentTimeMillis());

        // try to get the input stream (three times)
        InputStream is = null;
        for (int i = 0; i < 3; i++) {
            try {
                is = connection.getInputStream();
                break;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (is == null) {
            throw new ConnectException("Can't get '" + fileUrl + " to '" + file + "'");
        }

        // reade from remote resource and write to the local file
        FileOutputStream fos = new FileOutputStream(file.toFile());
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) >= 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        is.close();

        log.debug("Set last modified of '{}' to '{}'", file, lastModified);
        Files.setLastModifiedTime(file, FileTime.fromMillis(lastModified));

        validateDownload(fileUrl, file);
        return file;
    }

    /**
     * Succeeds if downloaded file exists and has size &gt; 0
     * <p>Override this method to provide your own validation rules such as content length matching or checksum checking etc</p>
     * @param originalUrl the source from which the file was downloaded
     * @param downloadedFile the path to the downloaded file
     * @throws PluginException if the validation failed
     */
    protected void validateDownload(URL originalUrl, Path downloadedFile) throws PluginException {
        try {
            if (Files.isRegularFile(downloadedFile) && Files.size(downloadedFile) > 0) {
                return;
            }
        } catch (IOException e) { /* Fallthrough */ }

        throw new PluginException("Failed downloading file {}", downloadedFile);
    }
    
}
