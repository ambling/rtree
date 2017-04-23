package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.kv.store.MapStore;
import com.github.davidmoten.rtree.kv.store.redis.RedisStore;

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

    public static KVStore redisStore(String hostName, String dataName){
        return new RedisStore(hostName, dataName);
    }
}
