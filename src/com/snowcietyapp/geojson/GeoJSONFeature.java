package com.snowcietyapp.geojson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mapsforge.core.model.LatLong;

public class GeoJSONFeature {
	
	public final static String GEOM_POINT = "Point";
	public final static String GEOM_MULTIPOINT = "MultiPoint";
	public final static String GEOM_LINESTRING = "LineString";
	public final static String GEOM_MULTILINESTRING = "MultiLineString";
	public final static String GEOM_POLYGON = "Polygon";
	public final static String GEOM_MULTIPOLYGON = "MultiPolygon";
	
	private List<LatLong> geometry;
	private HashMap<String, String> properties;
	private String geometryType;

	public GeoJSONFeature(List<LatLong> geometry, HashMap<String, String> properties) {
		this.geometry = geometry;
		this.properties = properties;
	}
	
	public GeoJSONFeature() {
		this.geometry = new ArrayList<LatLong>();
		this.properties = new HashMap<String, String>();
	}

	public List<LatLong> getGeometry() {
		return geometry;
	}

	public void setGeometry(List<LatLong> geometry) {
		this.geometry = geometry;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}
	
}
