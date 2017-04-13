package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometry;

public final class EntryOnKV<T, S extends Geometry> implements Entry<T, S> {
    KVStore<T, S> kvStore;
    S geometry;
    String key;

    EntryOnKV(S geometry, String key, KVStore<T, S> kvStore) {
        this.geometry = geometry;
        this.key = key;
        this.kvStore = kvStore;
    }

    @Override
    public S geometry() {
        return geometry;
    }

    @Override
    public T value() {
        return kvStore.getData(key);
    }
}
