package com.snowcietyapp.geojson;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.download.DownloadJob;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.model.MapViewPosition;


public class GeoJSONDownloadLayer extends GeoJSONTileLayer<DownloadJob> {
	private static final int DOWNLOAD_THREADS_MAX = 8;
	private static final Logger LOGGER = Logger.getLogger(GeoJSONDownloadLayer.class.getName());

	private final GeoJSONTileThread[] tileDownloadThreads;
	private final TileSource tileSource;
	private final FeaturePainter painter;

	public GeoJSONDownloadLayer(GeoJSONTileCache tileCache, MapViewPosition mapViewPosition, TileSource tileSource,
			LayerManager layerManager, FeaturePainter painter) {
		super(tileCache, mapViewPosition);

		if (tileSource == null) {
			throw new IllegalArgumentException("tileSource must not be null");
		} else if (layerManager == null) {
			throw new IllegalArgumentException("layerManager must not be null");
		}

		this.tileSource = tileSource;
		if (painter != null) {
			this.painter = painter;
		} else {
			this.painter = new DefaultPainter();
		}

		int numberOfDownloadThreads = Math.min(tileSource.getParallelRequestsLimit(), DOWNLOAD_THREADS_MAX);
		this.tileDownloadThreads = new GeoJSONTileThread[numberOfDownloadThreads];
		for (int i = 0; i < numberOfDownloadThreads; ++i) {
			this.tileDownloadThreads[i] = new GeoJSONTileThread(tileCache, this.jobQueue, layerManager);
		}
	}

	@Override
	public void destroy() {
		for (GeoJSONTileThread tileDownloadThread : this.tileDownloadThreads) {
			tileDownloadThread.interrupt();
		}
		super.destroy();
	}

	@Override
	public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
		if (zoomLevel < this.tileSource.getZoomLevelMin() || zoomLevel > this.tileSource.getZoomLevelMax()) {
			return;
		}

		super.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
	}
	
	@Override
	public void drawFeature(GeoJSONFeature feature, BoundingBox boundingBox,
			byte zoomLevel, Canvas canvas, Point topLeftPoint) {
		Layer l = painter.paint(feature);
		if (l == null) {
			throw new NullPointerException("The Layer returned from painter was null!");
		}
		l.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
	}

	public void start() {
		for (GeoJSONTileThread tileDownloadThread : this.tileDownloadThreads) {
			if (!tileDownloadThread.hasBeenStarted()) {
				LOGGER.log(Level.INFO, "Starting a download thread");
				tileDownloadThread.start();
			}
		}
	}

	@Override
	protected DownloadJob createJob(Tile tile) {
		return new DownloadJob(tile, this.tileSource);
	}

}
