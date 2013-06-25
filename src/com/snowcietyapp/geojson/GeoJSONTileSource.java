package com.snowcietyapp.geojson;

import org.mapsforge.map.layer.download.tilesource.TileSource;

public abstract class GeoJSONTileSource implements TileSource {

    protected final String hostName;
    protected final String path;
    protected final int port;

    protected GeoJSONTileSource(String hostName, String path, int port) {
            if (hostName == null || hostName.isEmpty()) {
                    throw new IllegalArgumentException("no host name specified");
            } else if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException("invalid port number: " + port);
            }

            this.hostName = hostName;
            this.path = path;
            this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
            if (this == obj) {
                    return true;
            } else if (!(obj instanceof GeoJSONTileSource)) {
                    return false;
            }
            GeoJSONTileSource other = (GeoJSONTileSource) obj;
            if (!this.hostName.equals(other.hostName)) {
                    return false;
            } else if (this.port != other.port) {
                    return false;
            }
            return true;
    }

    @Override
    public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.hostName.hashCode();
            result = prime * result + this.port;
            return result;
    }
    
    public String getPath() {
    	return this.path;
    }

}
