package com.snowcietyapp.geojson.application;

import java.net.MalformedURLException;
import java.net.URL;

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;

public class LeisureSource extends AbstractTileSource {

	private static final int PARALLEL_LIMIT = 1;
	private static final int ZOOM_MAX = 18;
	private static final int ZOOM_MIN = 12;
	
	private static final String PROTOCOL = "http";
	private static final String HOSTNAME = "192.168.1.96";
	private static final String PATH = "/tiles/tiles.py/leisure/";
	private static final int PORT = 80;

	/**
	 * @param hostName	DEPRECATED!
	 * @param port		DEPRECATED!
	 */
	protected LeisureSource(String hostName, int port) {
		super(HOSTNAME, PORT);
	}
	
	@Override
	public int getParallelRequestsLimit() {
		return PARALLEL_LIMIT;
	}

	@Override
	public URL getTileUrl(Tile tile) throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(PATH);
		sb.append(tile.zoomLevel);
		sb.append("/");
		sb.append(tile.tileX);
		sb.append("/");
		sb.append(tile.tileY);
		sb.append(".geojson");
		return new URL(PROTOCOL,HOSTNAME, PORT, sb.toString());
	}

	@Override
	public byte getZoomLevelMax() {
		return ZOOM_MAX;
	}

	@Override
	public byte getZoomLevelMin() {
		return ZOOM_MIN;
	}

}
