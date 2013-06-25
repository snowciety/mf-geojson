package com.snowcietyapp.geojson;

import org.mapsforge.map.layer.Layer;


/**
 * The FeaturePainter is used by the GeoJSONDownloadLayer to draw a specific feature.
 * Implementing and passing a FeaturePainter to the GeoJSONDownloadLayer gives the flexibility
 * of deciding how a feature shall be drawn.
 * An example of a FeaturePainter can be found in {@link DefaultPainter}.
 *
 */
public interface FeaturePainter {
	/**
	 * Returns the Layer that should be drawn for this specific feature.
	 * A Layer could be a Polyline, Polygon etc.
	 * @param 	feature
	 * @return	The layer to be drawn
	 */
	public Layer paint(GeoJSONFeature feature);
}
