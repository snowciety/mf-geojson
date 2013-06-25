package com.snowcietyapp.geojson;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.download.DownloadJob;
import org.mapsforge.map.layer.queue.JobQueue;
import org.mapsforge.map.util.PausableThread;


public class GeoJSONTileThread extends PausableThread {
	private static final Logger LOGGER = Logger.getLogger(GeoJSONTileThread.class.getName());

	private volatile boolean started = false;

	private final JobQueue<DownloadJob> jobQueue;
	private final LayerManager layerManager;
	private final GeoJSONTileCache tileCache;

	GeoJSONTileThread(GeoJSONTileCache tileCache, JobQueue<DownloadJob> jobQueue, LayerManager layerManager) {
		super();
		this.tileCache = tileCache;
		this.jobQueue = jobQueue;
		this.layerManager = layerManager;
	}

	@Override
	protected void doWork() throws InterruptedException {
		DownloadJob downloadJob = this.jobQueue.get();

		try {
			if (!this.tileCache.containsKey(downloadJob.tile)) {
				downloadTile(downloadJob);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			this.jobQueue.remove(downloadJob);
		}
	}

	@Override
	protected ThreadPriority getThreadPriority() {
		return ThreadPriority.BELOW_NORMAL;
	}

	@Override
	protected boolean hasWork() {
		return true;
	}

	@Override
	public synchronized void start() {
		super.start();
		started = true;
	}

	public boolean hasBeenStarted() {
		return started;
	}

	private void downloadTile(DownloadJob downloadJob) throws IOException {
		GeoJSONTileDownloader tileDownloader = new GeoJSONTileDownloader(downloadJob);
		JSONObject jsonObj = tileDownloader.downloadJson();
		if (!isInterrupted() && jsonObj != null) {
			this.tileCache.put(downloadJob.tile, jsonObj);
			this.layerManager.redrawLayers();
		}
	}
}
