package com.snowcietyapp.geojson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mapsforge.map.layer.download.DownloadJob;


public class GeoJSONTileDownloader {
	private static final int TIMEOUT_CONNECT = 5000;
	private static final int TIMEOUT_READ = 10000;
	private static final Logger LOGGER = Logger.getLogger(GeoJSONTileDownloader.class.getName());

	private static InputStream getInputStream(URLConnection urlConnection) throws IOException {
		if ("gzip".equals(urlConnection.getContentEncoding())) {
			return new GZIPInputStream(urlConnection.getInputStream());
		}
		return urlConnection.getInputStream();
	}

	private static URLConnection getURLConnection(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
		urlConnection.setReadTimeout(TIMEOUT_READ);
		return urlConnection;
	}

	private final DownloadJob downloadJob;

	GeoJSONTileDownloader(DownloadJob downloadJob) {
		if (downloadJob == null) {
			throw new IllegalArgumentException("downloadJob must not be null");
		}

		this.downloadJob = downloadJob;
	}

	JSONObject downloadJson() throws IOException {
		URL url = this.downloadJob.tileSource.getTileUrl(this.downloadJob.tile);
		URLConnection urlConnection = getURLConnection(url);
		LOGGER.log(Level.INFO, "Downloading JSON "+url.toString());
		InputStream inputStream;
		try {
			inputStream = getInputStream(urlConnection);
		} 
		catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, "JSON was not found on server!");
			return null;
		}
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, "UTF-8");
		String jsonString = writer.toString();
		JSONObject jsonObj = GeoJSONUtils.getGeoJSONObject(jsonString);

		IOUtils.closeQuietly(inputStream);
		if (jsonObj != null) {
			LOGGER.log(Level.INFO, "Downloaded JSON data");
		} else {
			LOGGER.log(Level.WARNING, "Downloaded something that was not JSON from "+url.toString());
		}
		return jsonObj;
	}
}