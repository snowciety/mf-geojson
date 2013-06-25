package com.snowcietyapp.geojson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mapsforge.core.model.Tile;

/**
 * Custom file cache with the following goal:
 * Store GeoJSON files on disk and use them when available.
 * 
 * Note!
 * The cacheDirectory passed to the constructor should be unique for
 * every GeoJSONTileSource. If not, the GeoJSOn files will be overwritten and
 * layers will not be drawn properly. Possible TODO would be to verify that
 * only one GeoJSONFileSystemTileCache is created per directory.
 *
 */
public class GeoJSONFileSystemTileCache implements GeoJSONTileCache {

	static final String FILE_EXTENSION = ".geojson";
	private static final Logger LOGGER = Logger.getLogger(GeoJSONFileSystemTileCache.class.getName());

	private static File checkDirectory(File file) {
		if (!file.exists() && !file.mkdirs()) {
			throw new IllegalArgumentException("could not create directory: " + file);
		} else if (!file.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + file);
		} else if (!file.canRead()) {
			throw new IllegalArgumentException("cannot read directory: " + file);
		} else if (!file.canWrite()) {
			throw new IllegalArgumentException("cannot write directory: " + file);
		}
		return file;
	}

	private final File cacheDirectory;

	/**
	 * @param capacity
	 *            the maximum number of entries in this cache.
	 * @param cacheDirectory
	 *            the directory where cached tiles will be stored. 
	 *            Make sure it is unique for every GeoJSONTileSource!
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public GeoJSONFileSystemTileCache(int capacity, File cacheDirectory) {
		this.cacheDirectory = checkDirectory(cacheDirectory);
	}

	public synchronized boolean containsKey(Tile key) {
		File f = getCachedFile(key);
		return f.exists();
	}

	public synchronized void destroy() {
		// Do nothing, for now.
	}
	
	public void clearDiskCache() {
		File[] filesToDelete = this.cacheDirectory.listFiles(GeoJSONFileNameFilter.INSTANCE);
		if (filesToDelete != null) {
			for (File file : filesToDelete) {
				if (file.exists() && !file.delete()) {
					LOGGER.log(Level.SEVERE, "could not delete file: " + file);
				}
				file = null;
			}
		}
		filesToDelete = null;
	}

	public synchronized JSONObject get(Tile key) {
		LOGGER.log(Level.INFO, "Getting tile "+key.toString());
		File file = getCachedFile(key);
		if (!file.exists()) {
			LOGGER.log(Level.WARNING, "Tile does not exist in cache!");
			return null;
		}

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			String jsonString = writer.toString();
			IOUtils.closeQuietly(inputStream);
			return GeoJSONUtils.getGeoJSONObject(jsonString);
		} catch (IOException e) {
			remove(key);
			LOGGER.log(Level.SEVERE, null, e);
			return null;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	private boolean remove(Tile key) {
		File f = getCachedFile(key);
		return f.delete();
	}

	public synchronized void put(Tile key, JSONObject jsonObj) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		} else if (jsonObj == null) {
			throw new IllegalArgumentException("bitmap must not be null");
		}

		OutputStream outputStream = null;
		try {
			File file = getCachedFile(key);
			if (file.exists()) {
				LOGGER.log(Level.WARNING, "Overwriting existing tile!");
			}
			outputStream = new FileOutputStream(file);
			outputStream.write(jsonObj.toString().getBytes());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
		} finally {
			IOUtils.closeQuietly(outputStream);
			LOGGER.log(Level.INFO, "Done putting JSON in cache");
		}
	}

	private File getCachedFile(Tile key) {
		String fileName = Byte.toString(key.zoomLevel) + "_" + Long.toString(key.tileX) + "_" + Long.toString(key.tileY);
		File file = new File(this.cacheDirectory, fileName + FILE_EXTENSION);
		return file;
	}
}
