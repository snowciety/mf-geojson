package com.snowcietyapp.geojson.application;

import java.util.List;

import org.mapsforge.core.model.LatLong;

import com.snowcietyapp.geojson.GeoJSONFeature;

public class PisteFeature extends GeoJSONFeature {
	public static final String TAG_NAME = "name";
	public static final String TAG_TYPE = "type";
	public static final String TAG_DIFFICULTY = "difficulty";
	public static final String TAG_REF = "ref";

	private String name;
	private String type;
	private String difficulty;
	private String ref;
	
	public PisteFeature() {
	}
	
	public PisteFeature(List<LatLong> geometry, String name, String type,
			String difficulty, String ref) {
		super(geometry, null);
		this.name = name;
		this.type = type;
		this.difficulty = difficulty;
		this.ref = ref;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	
}
