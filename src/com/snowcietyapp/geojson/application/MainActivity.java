package com.snowcietyapp.geojson.application;

import java.io.File;
import java.util.ArrayList;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.overlay.Polygon;
import org.mapsforge.map.layer.overlay.Polyline;

import com.snowcietyapp.geojson.application.R;
import com.snowcietyapp.geojson.FeaturePainter;
import com.snowcietyapp.geojson.GeoJSONDownloadLayer;
import com.snowcietyapp.geojson.GeoJSONFeature;
import com.snowcietyapp.geojson.GeoJSONFileSystemTileCache;
import com.snowcietyapp.geojson.GeoJSONTileSource;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * A sample Android application that uses mapsforge 0.4.x ("rewrite" branch) together
 * with a GeoJSON library created by Snowciety to draw maps based on GeoJSON data.
 * 
 * The application uses the OpenStreetMap (OSM) vector tiles as available here:
 * http://openstreetmap.us/~migurski/vector-datasource/
 *
 */
public class MainActivity extends Activity {
	
	private static final double LAT = 47.32;	//Zell am See
	private static final double LON = 12.79; 	//Zell am See
	//private static final double LAT = 52.373123; //Amsterdam
	//private static final double LON = 4.892564; //Amsterdam
	private static final byte ZOOMLEVEL = 15;
	private static final LatLong initPos = new LatLong(LAT, LON);
	
	private MapView mMapView;
	private LayerManager mLayerManager;
	private GeoJSONDownloadLayer mRoadLayer;
	private GeoJSONDownloadLayer mWaterLayer;
	private GeoJSONDownloadLayer mBuildingsLayer;
	
	private CenterDialog mCenterDialog;
	
	private ArrayList<GeoJSONFileSystemTileCache> mCaches = new ArrayList<GeoJSONFileSystemTileCache>();
	private ArrayList<GeoJSONDownloadLayer> mLayers = new ArrayList<GeoJSONDownloadLayer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMapView = new MapView(this);
		SharedPreferences sp = getSharedPreferences(this.getClass().getSimpleName(), MODE_PRIVATE);
		mMapView.getModel().init(new AndroidPreferences(sp));
		mMapView.getModel().mapViewPosition.setCenter(initPos);
		mMapView.getModel().mapViewPosition.setZoomLevel(ZOOMLEVEL);
		mMapView.setClickable(true);
		mMapView.setFocusable(true);
		mLayerManager = mMapView.getLayerManager();
		
		setContentView(mMapView);
		initWaterOsmLayer();
		initRoadOsmLayer();
		initBuildingsOsmLayer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		for (GeoJSONDownloadLayer layer : mLayers) {
			layer.start();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		for (GeoJSONDownloadLayer layer : mLayers) {
			layer.destroy();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_clear_cache:
				for (GeoJSONFileSystemTileCache cache : mCaches) {
					cache.clearDiskCache();
				}
				return true;
			case R.id.menu_set_center:
				if (mCenterDialog == null) {
					mCenterDialog = new CenterDialog();
				}
				mCenterDialog.show(getFragmentManager(), "dialog");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initWaterOsmLayer(){
		WaterOsmSource source = new WaterOsmSource(null, 0);
		mWaterLayer = new GeoJSONDownloadLayer(
				createTileCache(source), 
				mMapView.getModel().mapViewPosition, 
				source, 
				mLayerManager,
				new WaterPainter());
		mLayerManager.getLayers().add(mWaterLayer);
		mLayers.add(mWaterLayer);
	}
	
	private void initRoadOsmLayer(){
		RoadOsmSource source = new RoadOsmSource(null, 0);
		mRoadLayer = new GeoJSONDownloadLayer(
				createTileCache(source), 
				mMapView.getModel().mapViewPosition, 
				source, 
				mLayerManager,
				new RoadPainter());
		mLayerManager.getLayers().add(mRoadLayer);
		mLayers.add(mRoadLayer);
	}
	
	private void initBuildingsOsmLayer(){
		BuildingsOsmSource source = new BuildingsOsmSource(null, 0);
		mBuildingsLayer = new GeoJSONDownloadLayer(
				createTileCache(source), 
				mMapView.getModel().mapViewPosition, 
				source, 
				mLayerManager,
				new BuildingsPainter());
		mLayerManager.getLayers().add(mBuildingsLayer);
		mLayers.add(mBuildingsLayer);
	}
	
	private GeoJSONFileSystemTileCache createTileCache(GeoJSONTileSource source){
        String cacheDirectoryName = this.getExternalCacheDir().getAbsolutePath() + File.separator + this.getClass().getSimpleName() + File.separator + source.getPath();
        File cacheDirectory = new File(cacheDirectoryName);
        if (!cacheDirectory.exists()) {
                cacheDirectory.mkdir();
        }
        GeoJSONFileSystemTileCache cache = new GeoJSONFileSystemTileCache(1024, cacheDirectory);
        mCaches.add(cache);
        return cache;
	}
	
	public void setCenter(double lat, double lon){
		final LatLong pos = new LatLong(lat, lon);
		Log.d("MainActivity", "Setting center at "+pos.toString());
		mMapView.getModel().mapViewPosition.setCenter(pos);
		mMapView.getModel().mapViewPosition.setZoomLevel(ZOOMLEVEL);
	}
	
	private final static class WaterPainter implements FeaturePainter {

		@Override
		public Layer paint(GeoJSONFeature feature) {
			Paint paintStroke = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintStroke.setStyle(Style.STROKE);
			paintStroke.setColor(Color.BLACK);
			paintStroke.setStrokeWidth(1);

			Paint paintFill = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintFill.setStyle(Style.FILL);
			paintFill.setColor(Color.BLUE);
			

			Polygon pol = new Polygon(
					paintFill, 
					paintStroke, 
					AndroidGraphicFactory.INSTANCE);
			if (feature.getGeometry() != null) {
				pol.getLatLongs().addAll(
						feature.getGeometry());
			}
			return pol;
		}
		
	}
	
	private final static class RoadPainter implements FeaturePainter {

		@Override
		public Layer paint(GeoJSONFeature feature) {
			Paint paintStroke = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintStroke.setStyle(Style.STROKE);
			paintStroke.setColor(Color.GRAY);
			paintStroke.setStrokeWidth(5);

			Polyline pl = new Polyline(paintStroke,
					AndroidGraphicFactory.INSTANCE);
			if (feature.getGeometry() != null) {
				pl.getLatLongs().addAll(
						feature.getGeometry());
			}
			return pl;
		}
		
	}
	
	private final static class BuildingsPainter implements FeaturePainter {

		@Override
		public Layer paint(GeoJSONFeature feature) {
			Paint paintStroke = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintStroke.setStyle(Style.STROKE);
			paintStroke.setColor(Color.BLACK);
			paintStroke.setStrokeWidth(1);

			Paint paintFill = AndroidGraphicFactory.INSTANCE
					.createPaint();
			paintFill.setStyle(Style.FILL);
			paintFill.setColor(Color.RED);
			

			Polygon pol = new Polygon(
					paintFill, 
					paintStroke, 
					AndroidGraphicFactory.INSTANCE);
			if (feature.getGeometry() != null) {
				pol.getLatLongs().addAll(
						feature.getGeometry());
			}
			return pol;
		}
		
	}
}
