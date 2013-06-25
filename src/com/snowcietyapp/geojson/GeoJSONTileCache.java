package com.snowcietyapp.geojson;

import java.util.Map;

import org.json.JSONObject;
import org.mapsforge.core.model.Tile;

/**
 * Interface for tile GeoJSON caches.
 */
public interface GeoJSONTileCache {
        /**
         * @return true if this cache contains a JSON object for the given key, false otherwise.
         * @see Map#containsKey
         */
        boolean containsKey(Tile key);

        /**
         * Destroys this cache.
         */
        void destroy();

        /**
         * @return the image for the given key or null, if this cache contains no JSON object for the key.
         * @see Map#get
         */
        JSONObject get(Tile key);
        
        /**
         * @throws IllegalArgumentException
         *             if any of the parameters is {@code null}.
         * @see Map#put
         */
        void put(Tile key, JSONObject jsonObj);
}
