package com.snowcietyapp.geojson;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerUtil;
import org.mapsforge.map.layer.TilePosition;
import org.mapsforge.map.layer.queue.Job;
import org.mapsforge.map.layer.queue.JobQueue;
import org.mapsforge.map.model.MapViewPosition;


public abstract class GeoJSONTileLayer<T extends Job> extends Layer {
	protected final JobQueue<T> jobQueue;
	private final GeoJSONTileCache tileCache;
	private static final Logger LOGGER = Logger.getLogger(GeoJSONTileLayer.class.getName());

	public GeoJSONTileLayer(GeoJSONTileCache tileCache, MapViewPosition mapViewPosition) {
		super();

		if (tileCache == null) {
			throw new IllegalArgumentException("tileCache must not be null");
		} else if (mapViewPosition == null) {
			throw new IllegalArgumentException("mapViewPosition must not be null");
		}

		this.tileCache = tileCache;
		this.jobQueue = new JobQueue<T>(mapViewPosition);
	}

	@Override
	public void destroy() {
		this.tileCache.destroy();
	}

	@Override
	public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
		ArrayList<TilePosition> tilePositions = LayerUtil.getTilePositions(boundingBox, zoomLevel, topLeftPoint);
		
		for (int i = tilePositions.size() - 1; i >= 0; --i) {
			TilePosition tilePosition = tilePositions.get(i);
			Tile tile = tilePosition.tile;
			JSONObject jsonObj = this.tileCache.get(tile);
			if (jsonObj == null) {
				LOGGER.log(Level.INFO, "Tile not in cache");
				this.jobQueue.add(createJob(tile));
				drawParentTile(canvas, topLeftPoint, tile, boundingBox);
			} else {
				LOGGER.log(Level.INFO, "Tile in cache");
				ArrayList<GeoJSONFeature> features = GeoJSONUtils.getGeoJSONFeatures(jsonObj);
				for (GeoJSONFeature feature : features) {
					drawFeature(feature, boundingBox, zoomLevel, canvas, topLeftPoint);
				}
			}
		}

		this.jobQueue.notifyWorkers();
	}

	protected abstract T createJob(Tile tile);

	private void drawParentTile(Canvas canvas, Point point, Tile tile, BoundingBox boundingBox) {
		Tile cachedParentTile = getCachedParentTile(tile, 4);
		if (cachedParentTile != null) {
			LOGGER.log(Level.INFO, "Found parent tile");
			JSONObject jsonObj = this.tileCache.get(cachedParentTile);
			if (jsonObj != null) {
				LOGGER.log(Level.INFO, "Found parent JSON object");
				Point pt = new Point(cachedParentTile.tileX, cachedParentTile.tileY);

				canvas.setClip((int)cachedParentTile.tileX, (int)cachedParentTile.tileY, Tile.TILE_SIZE, Tile.TILE_SIZE);
				ArrayList<GeoJSONFeature> features = GeoJSONUtils.getGeoJSONFeatures(jsonObj);
				for (GeoJSONFeature feature : features) {
					drawFeature(feature, boundingBox, cachedParentTile.zoomLevel, canvas, pt);
				}
				canvas.resetClip();
			}
		}
	}

	/**
	 * @return the first parent object of the given object whose tileCacheBitmap is cached (may be null).
	 */
	private Tile getCachedParentTile(Tile tile, int level) {
		if (level == 0) {
			return null;
		}

		Tile parentTile = tile.getParent();
		if (parentTile == null) {
			return null;
		} else if (this.tileCache.containsKey(parentTile)) {
			return parentTile;
		}

		return getCachedParentTile(parentTile, level - 1);
	}

	
	/**
	 * Draws something on the canvas based on the information in the feature supplied.
	 * @param feature
	 * @param boundingBox
	 * @param zoomLevel
	 * @param canvas
	 * @param topLeftPoint
	 */
	public abstract void drawFeature(GeoJSONFeature feature, BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint);
}
