package com.snowcietyapp.geojson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.model.LatLong;


public class GeoJSONUtils {
	
	private static final String TAG_FEATURES = "features";
	private static final String TAG_GEOMETRY = "geometry";
	private static final String TAG_TYPE = "type";
	private static final String TAG_PROPERTIES = "properties";
	private static final String TAG_COORDINATES = "coordinates";
	private static final Logger LOGGER = Logger.getLogger(GeoJSONUtils.class.getName());

	/**
	 * Returns a JSOn object from a string.
	 * Merely a wrapper for JSONObject that handles exceptions.
	 * 
	 * @param 	jsonString
	 * @return	the JSON object. Null if jsonString is not JSON.
	 */
	public static JSONObject getGeoJSONObject(String jsonString) {
		if (!jsonString.startsWith("{")) {
			//Not proper JSON, abort!
			return null;
		}
		JSONObject apa;
		try {
			apa  = new JSONObject(jsonString);
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
		return apa;
	}
	
	/**
	 * Returns a list of GeoJSOnFeatures from a JSON object.
	 * The GeoJSONFeature objects contains geometry and properties.
	 * 
	 * Note! This implementation assumes that it is dealing with
	 * a FeatureCollection and that the GeoJSON string looks
	 * like the following: http://tile.openstreetmap.us/vectiles-skeletron/12/656/1582.json
	 * 
	 * @param jsonObj	The JSON object to parse.
	 * @return
	 */
	public static ArrayList<GeoJSONFeature> getGeoJSONFeatures(JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		
		ArrayList<JSONObject> features = getFeatures(jsonObj);
		if (features == null) {
			return null;
		}
		
		ArrayList<GeoJSONFeature> outFeatures = new ArrayList<GeoJSONFeature>();
		for (JSONObject feature : features) {
			List<LatLong> geometry;
			try {
				geometry = getLatLongList(feature);
			} catch (JSONException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
				geometry = null;
			}
			HashMap<String, String> properties = getProperties(feature);
			GeoJSONFeature f = new GeoJSONFeature(geometry, properties);
			f.setGeometryType(GeoJSONUtils.getGeometryType(feature));
			outFeatures.add(f);
		}
		
		return outFeatures;
	}
	
	/**
	 * Returns a list of features in a feature collection.
	 * Note that the features are JSON objects.
	 * 
	 * @param jsonObj	the JSON object to parse from.
	 * @return			a list of features.
	 */
	public static ArrayList<JSONObject> getFeatures(JSONObject jsonObj) {
		JSONArray jfeatures;
		try {
			jfeatures = jsonObj.getJSONArray(TAG_FEATURES);
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "Features could not be found");
			return null;
		}
		ArrayList<JSONObject> out = new ArrayList<JSONObject>();
		
		for (int i = 0; i < jfeatures.length(); i++) {
			try {
				out.add(jfeatures.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return out;
	}
	
	/**
	 * Gets the geometric type of a feature.
	 * Types are of type "LineString", "MultiLineString", "Polygon" etc.
	 * 
	 * @param 	feature
	 * @return	the type as a string.
	 */
	public static String getGeometryType(JSONObject feature) {
		JSONObject geom = getGeometry(feature);
		
		try {
			return geom.getString(TAG_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the properties of a feature object (JSON)
	 * 
	 * @param feature	the JSON object
	 */
	public static HashMap<String, String> getProperties(JSONObject feature) {
		JSONObject propObj;
		//Get the properties
		try {
			propObj = feature.getJSONObject(TAG_PROPERTIES);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		HashMap<String, String> properties = new HashMap<String, String>();
		Iterator<?> keys = propObj.keys();
		
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value;
			try {
				value = propObj.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
				break;
			}
			properties.put(key, value);
			
		}
		
		return properties;
	}
	
	
	/**
	 * Returns coordinates array of a feature JSON object
	 * 
	 * @param 	feature
	 * @return	a JSON array of coordinates.
	 */
	private static JSONArray getCoordinates(JSONObject feature) {
		JSONObject geom = getGeometry(feature);
		
		JSONArray coord = null;
		try {
			coord = geom.getJSONArray(TAG_COORDINATES);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return coord;
	}
	
	/**
	 * Returns the geometry object of a feature object.
	 * 
	 * @param 	feature
	 * @return	the geometry JSON object.
	 */
	private static JSONObject getGeometry(JSONObject feature) {
		try {
			return feature.getJSONObject(TAG_GEOMETRY);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Dumps all geometric points of a feature to logger.
	 * 
	 * @param LatLongs	The coordinates to dump.
	 */
	public static void dumpLatLongs(GeoJSONFeature feature) {
		List<LatLong> LatLongs = feature.getGeometry();
		if ( LatLongs != null) {
			for (LatLong gp : LatLongs) {
				LOGGER.log(Level.INFO, gp.toString());
			}
		} else {
			LOGGER.log(Level.SEVERE,"The given list is null!");
		}
	}
	
	/**
	 * Dumps all properties for a GeoJSONFeature to logger.
	 * 
	 * @param feature	The feature to dump.
	 */
	public static void dumpProperties(GeoJSONFeature feature) {
		HashMap<String, String> props = feature.getProperties();
		dumpProperties(props);
	}
	
	/**
	 * Dumps all properties for a property map.
	 * 
	 * @param props		The property map to dump.
	 */
	public static void dumpProperties(HashMap<String, String> props) {
		if (props == null) {
			LOGGER.log(Level.SEVERE, "Feature has no properties!");
			return;
		}
		Iterator<Entry<String, String>> it = props.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			LOGGER.log(Level.INFO, "Property "+entry.getKey()+" = "+entry.getValue());
		}
	}
	
	/**
	 * Get the a list of geometric points for a feature.
	 * These points are found in feature->geometry->coordinates
	 * 
	 * @param 	feature
	 * @return	a list of geometric points.
	 * @throws JSONException 
	 */
	public static List<LatLong> getLatLongList(JSONObject feature) throws JSONException {
		if (feature == null) {
			return null;
		}
		JSONArray coords = getCoordinates(feature);
		if(getGeometryType(feature).equals(GeoJSONFeature.GEOM_POINT)) {
			return getLatLong(coords, -1);
		} else if (getGeometryType(feature).equals(GeoJSONFeature.GEOM_LINESTRING)) {
			return getLatLong(coords, 0);
		} else if(getGeometryType(feature).equals(GeoJSONFeature.GEOM_POLYGON)) {
			return getLatLong(coords, 1);
		} else if(getGeometryType(feature).equals(GeoJSONFeature.GEOM_MULTIPOINT)) {
			return getLatLong(coords, 0);
		} else if(getGeometryType(feature).equals(GeoJSONFeature.GEOM_MULTILINESTRING)) {
			return getLatLong(coords, 1);
		} else if(getGeometryType(feature).equals(GeoJSONFeature.GEOM_MULTIPOLYGON)) {
			return getLatLong(coords, 2);
		} else {	
			return getLatLong(coords, -1);
		}
	}
	
	private static List<LatLong> getLatLong(JSONArray coords, int level) throws JSONException {
		// [[1,2],[3,4]]
		List<LatLong> outList = new ArrayList<LatLong>();
		if (level > 0) {
			for (int i = 0; i < coords.length(); i++) {
				outList.addAll(getLatLong(coords.getJSONArray(i), level-1));
			}
			return outList;
		} else if (level == 0){
			List<LatLong> outList2 = new ArrayList<LatLong>();
			for (int j = 0; j < coords.length(); j++) {
				outList2.add(simplestCoord(coords.getJSONArray(j)));
			}
			return outList2;
		} else {
			outList.add(simplestCoord(coords));
			return outList;
		}
	}
	
	private static LatLong simplestCoord(JSONArray coord) throws JSONException {
		// [1,2]
		LatLong gp = new LatLong(coord.getDouble(1), coord.getDouble(0));
		return gp;
	}
	

}
