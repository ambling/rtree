package com.github.davidmoten.rtree.kv;

/**
 * A static helper class to create various KVStore.
 */
public final class KVStoreHelper {

    private KVStoreHelper() {
        // prevent instantiation
    }

    public static KVStore mapStore() {
        return new MapStore();
    }
}
