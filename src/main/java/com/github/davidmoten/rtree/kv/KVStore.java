package com.github.davidmoten.rtree.kv;

/**
 * The interface of a key-value store.
 *
 * To enable most implementations of storage engine, the interface fixes the type of key and value to be String.
 */
public interface KVStore {

    String get(String key);

    void set(String key, String value);

}
