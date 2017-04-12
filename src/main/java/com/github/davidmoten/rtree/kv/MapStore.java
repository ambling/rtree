package com.github.davidmoten.rtree.kv;

import java.util.HashMap;

/**
 * A simple KVStore that backed by a hashmap.
 */
public class MapStore implements KVStore {
    HashMap<String, String> map;

    public MapStore() {
        map = new HashMap<String, String>();
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void set(String key, String value) {
        map.put(key, value);
    }
}
