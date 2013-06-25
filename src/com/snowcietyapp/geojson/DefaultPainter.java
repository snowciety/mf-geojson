package com.snowcietyapp.geojson;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Polygon;
import org.mapsforge.map.layer.overlay.Polyline;


/**
 * Draws a blue line for a LineString and gives a Polygon a green color with
 * a black stroke.
 *
 */
public class DefaultPainter implements FeaturePainter {

	@Override
	public Layer paint(GeoJSONFeature feature) {
		if (feature.getGeometryType().equals(GeoJSONFeature.GEOM_LINESTRING)) {
			Paint paintStroke = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintStroke.setStyle(Style.STROKE);
			paintStroke.setColor(Color.BLUE);
			paintStroke.setStrokeWidth(5);

			Polyline pl = new Polyline(paintStroke,
					AndroidGraphicFactory.INSTANCE);
			if (feature.getGeometry() != null) {
				pl.getLatLongs().addAll(
						feature.getGeometry());

				return pl;
			}
		}
		else if (feature.getGeometryType().equals(GeoJSONFeature.GEOM_POLYGON)) {
			Paint paintStroke = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintStroke.setStyle(Style.STROKE);
			paintStroke.setColor(Color.BLACK);
			paintStroke.setStrokeWidth(1);

			Paint paintFill = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintFill.setStyle(Style.FILL);
			paintFill.setColor(Color.GREEN);
			

			Polygon pol = new Polygon(
					paintFill, 
					paintStroke, 
					AndroidGraphicFactory.INSTANCE);
			pol.getLatLongs().addAll(
					feature.getGeometry());
			return pol;
		}
		return null;
	}

}
