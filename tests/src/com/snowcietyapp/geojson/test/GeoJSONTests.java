package com.snowcietyapp.geojson.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONObject;

import com.snowcietyapp.geojson.GeoJSONFeature;
import com.snowcietyapp.geojson.GeoJSONUtils;

import android.test.InstrumentationTestCase;
import android.util.Log;

public class GeoJSONTests extends InstrumentationTestCase {

	
	/**
	 * Tests the String-to-JSON wrapper.
	 */
	public void testGetJsonObject() {
		String jsonString = getTestJsonData("geometry-examples.geojson");
		JSONObject jsonObj = GeoJSONUtils.getGeoJSONObject(jsonString);
		assertTrue("Object should not be null!", jsonObj != null);
	}
	
	/**
	 * Tests if the parsing utilities can parse the GeoJSON features
	 * mentioned in the GeoJSON specification 1.0:
	 * http://www.geojson.org/geojson-spec.html#appendix-a-geometry-examples
	 * 
	 */
	public void testExamples() {
		String jsonString = getTestJsonData("geometry-examples.geojson");
		JSONObject jsonObj = GeoJSONUtils.getGeoJSONObject(jsonString);
		ArrayList<GeoJSONFeature> features = GeoJSONUtils.getGeoJSONFeatures(jsonObj);
		assertEquals(features.size(), 6);
		for (GeoJSONFeature feature : features) {
			GeoJSONUtils.dumpProperties(feature);
			GeoJSONUtils.dumpLatLongs(feature);
			assertTrue(feature.getGeometry() != null);
		}
	}
	
	private String getTestJsonData(String fileName) {
		String out;
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getInstrumentation().getContext().getAssets().open(fileName), "UTF-8"));
			while ((out = reader.readLine()) != null) {
				sb.append(out);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("ScMaps", "could not open asset file!");
		}
		return sb.toString();
	}
}
